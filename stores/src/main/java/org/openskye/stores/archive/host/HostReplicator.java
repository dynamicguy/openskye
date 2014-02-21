package org.openskye.stores.archive.host;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.SkyeException;
import org.openskye.domain.Node;
import org.openskye.domain.NodeArchiveStoreInstance;
import org.openskye.domain.NodeRole;
import org.openskye.domain.Project;
import org.openskye.node.NodeManager;
import org.openskye.replicate.Replicator;

import java.io.*;

/**
 * An implementation of a {@link org.openskye.replicate.Replicator} for the {@link org.openskye.stores.archive.host.HostArchiveStore}
 */
@Slf4j
public class HostReplicator implements Replicator {

    private final org.openskye.stores.archive.host.HostArchiveStore archiveStore;

    public HostReplicator(org.openskye.stores.archive.host.HostArchiveStore HostArchiveStore) {
        log.info("Creating replicator for " + HostArchiveStore);
        this.archiveStore = HostArchiveStore;
    }

    @Override
    public void replicate(Node targetNode, Project project) {
        // First we need to identify the node that is the primary
        // for this archive store

        log.debug("Replicator starting on node " + targetNode);

        checkNode(targetNode);

        Node primaryNode = getPrimaryNode();

        log.debug("Primary node is " + primaryNode);

        // Find all the ACB's that are on the primary but missing from the secondary

        Iterable<ArchiveContentBlock> acbs = archiveStore.getOmr().getACBsForReplication(primaryNode, NodeManager.getNode(), project);


        // Connect up

        JSch jsch = new JSch();

        try {
            String knownHosts = System.getProperty("user.home") + "/.ssh/known_hosts";
            String identity = System.getProperty("user.home") + "/.ssh/id_rsa";
            log.debug("Using SSH known hosts in " + knownHosts);
            log.debug("Adding identity " + identity);

            jsch.setKnownHosts(knownHosts);
            jsch.addIdentity(identity);

            log.debug("Creating session to " + primaryNode.getServiceAccount() + "@" + primaryNode.getHostname());
            Session session = jsch.getSession(primaryNode.getServiceAccount(), primaryNode.getHostname(), 22);

            session.connect();

            // Move the ACB's that are missing from the primary to the secondary
            for (ArchiveContentBlock acb : acbs) {
                log.debug("Replicating ACB " + acb);

                acb.getNodes().add(targetNode);
                archiveStore.getOmr().put(acb);
                File acbPath = archiveStore.getAcbPath(acb, false);

                // exec 'scp -f rfile' remotely
                String command = "scp -f " + acbPath.getAbsoluteFile();
                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);

                try {
                    // get I/O streams for remote scp
                    OutputStream out = channel.getOutputStream();
                    InputStream in = channel.getInputStream();

                    channel.connect();

                    byte[] buf = new byte[1024];

                    // send '\0'
                    buf[0] = 0;
                    out.write(buf, 0, 1);
                    out.flush();

                    while (true) {
                        int c = checkAck(in);
                        if (c != 'C') {
                            break;
                        }

                        // read '0644 '
                        in.read(buf, 0, 5);

                        long filesize = 0L;
                        while (true) {
                            if (in.read(buf, 0, 1) < 0) {
                                // error
                                break;
                            }
                            if (buf[0] == ' ') break;
                            filesize = filesize * 10L + (long) (buf[0] - '0');
                        }

                        String file = null;
                        for (int i = 0; ; i++) {
                            in.read(buf, i, 1);
                            if (buf[i] == (byte) 0x0a) {
                                file = new String(buf, 0, i);
                                break;
                            }
                        }

                        //System.out.println("filesize="+filesize+", file="+file);

                        // send '\0'
                        buf[0] = 0;
                        out.write(buf, 0, 1);
                        out.flush();

                        // read a content of lfile
                        FileOutputStream fos = new FileOutputStream(acbPath);
                        int foo;
                        while (true) {
                            if (buf.length < filesize) foo = buf.length;
                            else foo = (int) filesize;
                            foo = in.read(buf, 0, foo);
                            if (foo < 0) {
                                // error
                                break;
                            }
                            fos.write(buf, 0, foo);
                            filesize -= foo;
                            if (filesize == 0L) break;
                        }
                        fos.close();

                        if (checkAck(in) != 0) {
                            System.exit(0);
                        }

                        // send '\0'
                        buf[0] = 0;
                        out.write(buf, 0, 1);
                        out.flush();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new SkyeException("Unable to find ACB " + acb + " on " + primaryNode.getHostname(), e);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new SkyeException("Unable to copy to find ACB " + acb + " from " + primaryNode.getHostname(), e);
                }
            }

            session.disconnect();


        } catch (JSchException e) {
            e.printStackTrace();
            throw new SkyeException("Unable to connect to " + primaryNode.getHostname() + " as " + primaryNode.getServiceAccount(), e);
        }


        log.debug("Replicator complete");

    }

    private void checkNode(Node targetNode) {
        log.debug("Checking target node is " + targetNode + " and we are node " + NodeManager.getNode());
        if (!targetNode.equals(NodeManager.getNode())) {
            throw new SkyeException("You must run replication on the target node");
        }

        for (NodeArchiveStoreInstance nodeInstance : archiveStore.getArchiveStoreInstance().getNodes()) {
            if (targetNode.getId().equals(nodeInstance.getNode().getId())) {
                if (!NodeRole.REPLICA.equals(nodeInstance.getNodeRole()))
                    throw new SkyeException("Replication can not be run on a node that does not have the role REPLICA for the given archive store, this looks like it is the primary");
                return;
            }
        }
        throw new SkyeException("Replication must be run on a node with a role of REPLICA for the given archive store");
    }

    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

    public Node getPrimaryNode() {
        log.info("Archive store instance has " + archiveStore.getArchiveStoreInstance().getNodes().size() + " nodes");
        for (NodeArchiveStoreInstance nodeInstance : archiveStore.getArchiveStoreInstance().getNodes()) {
            if (NodeRole.PRIMARY.equals(nodeInstance.getNodeRole())) {
                log.debug("Identified primary as " + nodeInstance);
                return nodeInstance.getNode();
            }
        }
        throw new SkyeException("No primary node has been defined");
    }
}
