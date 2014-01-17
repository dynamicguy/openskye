package org.openskye.cli;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * A CliExitCodeException is not really an exception in the usual sense.  It is used by ExecutableCommand
 * subclasses to signal to the SkyeCli application that the final exit status should be nonzero.  This is
 * useful in multi-command scripts, where the exit status of a call to SkyeCli can be used to make decisions
 * based on the status of Skye, such as whether a previously submitted Task has completed yet.
 */
@Data
public class CliExitCodeException extends RuntimeException {
    @Getter
    @Setter
    private Integer exitCode;

    public CliExitCodeException(String message, Integer exitCode) {
        super(message);
        this.exitCode = exitCode;
    }
}

