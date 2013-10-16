package org.skye.stores.information.cifs;

import jcifs.smb.SmbFile;
import org.skye.core.ContainerObject;

/**
 * The representation of a folder on a CIFS server
 */
public class CifsContainerObject extends ContainerObject {
    public SmbFile smbFile;
}
