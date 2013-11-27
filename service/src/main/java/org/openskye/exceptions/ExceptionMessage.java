package org.openskye.exceptions;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * The representation of an exception that *must* be returned by an
 * exception mapper
 */
@Data
public class ExceptionMessage {

    private int errorCode;
    private String message;
    private String detail;
    private List<ExceptionMessageDetail> details = new ArrayList<>();

    public ExceptionMessage(int errorCode, String message, String detail) {
        this.errorCode = errorCode;
        this.message = message;
        this.detail = detail;
    }

    public ExceptionMessage(int errorCode, String message, String detail, List<ExceptionMessageDetail> details) {
        this(errorCode, message, detail);
        this.details = details;
    }


    public ExceptionMessage() {
        // No arg constructor
    }
}
