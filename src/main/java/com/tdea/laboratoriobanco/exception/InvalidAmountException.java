package com.tdea.laboratoriobanco.exception;

/** Invalid monetary amount. */
public class InvalidAmountException extends RuntimeException {
    /** Creates the business error. */
    public InvalidAmountException() {
        super("Invalid monetary amount");
    }
}
