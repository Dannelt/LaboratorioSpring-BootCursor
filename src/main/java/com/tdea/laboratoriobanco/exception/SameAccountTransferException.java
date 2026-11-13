package com.tdea.laboratoriobanco.exception;

/** Source and destination must differ. */
public class SameAccountTransferException extends RuntimeException {
    /** Creates the business error. */
    public SameAccountTransferException() {
        super("Source and destination must differ");
    }
}
