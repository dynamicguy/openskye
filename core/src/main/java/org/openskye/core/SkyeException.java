package org.openskye.core;

/**
 * A Skye exception
 */
public class SkyeException extends RuntimeException {

    public SkyeException(String s, Exception e) {
        super(s, e);
    }

    public SkyeException(String s) {
        super(s);
    }

}
