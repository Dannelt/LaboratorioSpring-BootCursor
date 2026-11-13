package com.tdea.laboratoriobanco.exception;

/** Account not found. */
public class AccountNotFoundException extends RuntimeException {
    /** Creates the business error. */
    public AccountNotFoundException() {
        super("Account not found");
    }
}
