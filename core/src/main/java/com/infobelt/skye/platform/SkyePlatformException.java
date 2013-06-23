package com.infobelt.skye.platform;

/**
 * A Skye platform exception
 */
public class SkyePlatformException extends RuntimeException {
    public SkyePlatformException(String s, Exception e) {
        super(s, e);
    }
}
