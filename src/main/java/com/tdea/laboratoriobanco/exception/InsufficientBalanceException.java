package com.tdea.laboratoriobanco.exception;

/** Insufficient balance. */
public class InsufficientBalanceException extends RuntimeException {
    /** Creates the business error. */
    public InsufficientBalanceException() {
        super("Insufficient balance");
    }
}
