package com.tdea.laboratoriobanco.entity;

/** Lifecycle states of a bank transfer. */
public enum TransferStatus {
    /** Registered but not completed. */
    PENDING,
    /** Successfully completed. */
    COMPLETED,
    /** Could not be completed. */
    FAILED
}
