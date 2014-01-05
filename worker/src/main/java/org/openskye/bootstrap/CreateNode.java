package org.openskye.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.openskye.domain.Node;
import org.openskye.domain.dao.NodeDAO;

import javax.inject.Inject;

/**
 * This will create the node entry for the host
 */
@Slf4j
public class CreateNode {

    @Inject
    private NodeDAO nodeDAO;

    public Node createNode(String hostname) {
        nodeDAO.getEntityManagerProvider().get().getTransaction().begin();

        Node node = new Node();
        node.setHostname(hostname);
        node.setServiceAccount(System.getProperty("user.name"));
        nodeDAO.create(node);

        nodeDAO.getEntityManagerProvider().get().getTransaction().commit();

        return node;
    }
}
