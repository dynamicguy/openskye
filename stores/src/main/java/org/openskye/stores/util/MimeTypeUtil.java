package org.openskye.stores.util;

import org.apache.tika.Tika;
import org.openskye.core.SkyeException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * A little utility to get the mimetype
 */
public class MimeTypeUtil {

    private static final Tika tika = new Tika();

    public static String getContentType(Path path) {
        try {
            return tika.detect(path.toFile());
        } catch (IOException e) {
            throw new SkyeException("Unable to identify mimetype on path " + path, e);
        }
    }

    public static String getContentType(InputStream in){
        try {
            return tika.detect(in);
        } catch (IOException e) {
            throw new SkyeException("Unable to identify mimetype", e);
        }
    }

    public static String getContentType(byte[] bytes){
        return tika.detect(bytes);
    }
}
