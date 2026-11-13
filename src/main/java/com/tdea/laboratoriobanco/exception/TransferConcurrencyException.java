package com.tdea.laboratoriobanco.exception;

/** A transfer could not obtain or retain consistent account access. */
public class TransferConcurrencyException extends RuntimeException {
    /**
     * Creates a concurrency error preserving the cause.
     * @param cause the persistence conflict
     */
    public TransferConcurrencyException(Throwable cause) {
        super("Concurrent account update; retry the operation", cause);
    }
}
