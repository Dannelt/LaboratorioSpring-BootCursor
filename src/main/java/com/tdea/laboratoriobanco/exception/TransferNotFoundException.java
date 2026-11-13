package com.tdea.laboratoriobanco.exception;

/** No transfer matches the requested receipt reference. */
public class TransferNotFoundException extends RuntimeException {
    /** Creates the missing transfer error. */
    public TransferNotFoundException() {
        super("Transfer not found");
    }
}
