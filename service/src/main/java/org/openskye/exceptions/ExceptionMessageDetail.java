package org.openskye.exceptions;

import lombok.Data;

/**
 * A repeatable detail entry for an exception
 */
@Data
public class ExceptionMessageDetail {

    private String path;
    private String message;
}
