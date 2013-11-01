package org.openskye.exceptions;

import lombok.Data;

/**
 * The representation of an exception that *must* be returned by an
 * exception mapper
 */
@Data
public class ExceptionMessage {

    private int errorCode;
    private String message;
    private String detail;

    public ExceptionMessage(int errorCode, String message, String detail) {
        this.errorCode = errorCode;
        this.message = message;
        this.detail = detail;
    }

    public ExceptionMessage() {
        // No arg constructor
    }
}
