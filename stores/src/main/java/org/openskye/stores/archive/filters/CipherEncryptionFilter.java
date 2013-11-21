package org.openskye.stores.archive.filters;

import org.openskye.core.ObjectStreamFilter;
import org.openskye.core.SkyeException;
import org.openskye.domain.ArchiveStoreDefinition;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;

/**
 * An implementation of the cipher encryption for the
 */
public class CipherEncryptionFilter implements ObjectStreamFilter {

    protected Cipher cipher = null;

    @Override
    public void initialize(ArchiveStoreDefinition archiveStoreDefinition) {
        try {
            byte[] keyBytes = "1234123412341234".getBytes();  //TODO: make this more robust
            final byte[] ivBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                    0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
            final SecretKey key = new SecretKeySpec(keyBytes, "AES");
            final IvParameterSpec IV = new IvParameterSpec(ivBytes);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, IV);
        } catch( Exception e ) {
            throw new SkyeException("Unable to initialize CipherEncryptionFilter",e);
        }
    }

    @Override
    public InputStream process(InputStream inputStream) {
        if ( cipher == null ) {
            throw new SkyeException("CipherEncryptionFilter is not initialized");
        } else {
            return new CipherInputStream(inputStream,cipher);
        }
    }

    @Override
    public InputStream unprocess(InputStream inputStream) {
        return process(inputStream);  //AES is symmetric, encrypting an encrypted stream decrypts it
    }
}
