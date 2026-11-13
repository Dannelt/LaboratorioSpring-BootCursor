package com.tdea.laboratoriobanco.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import jakarta.validation.constraints.*;

/** Persistent bank transfer. */
@Entity
@Table(name = "bank_transfers")
public class BankTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true, updatable = false)
    private UUID reference;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_account_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bank_transfers_source"))
    private BankAccount sourceAccount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_account_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bank_transfers_destination"))
    private BankAccount destinationAccount;

    @NotNull
    @Positive
    @Digits(integer = 17, fraction = 2)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransferStatus status;

    protected BankTransfer() {
        // Required by JPA.
    }

    /**
     * Creates a bank transfer.
     * @param sourceAccount the source account
     * @param destinationAccount the destination account
     * @param amount the amount
     */
    public BankTransfer(BankAccount sourceAccount, BankAccount destinationAccount, BigDecimal amount) {
        this.reference = UUID.randomUUID();
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.createdAt = Instant.now();
        this.status = TransferStatus.PENDING;
    }

    /**
     * Returns the id.
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the reference.
     * @return the reference
     */
    public UUID getReference() {
        return reference;
    }

    /**
     * Returns the source account.
     * @return the source account
     */
    public BankAccount getSourceAccount() {
        return sourceAccount;
    }

    /**
     * Returns the destination account.
     * @return the destination account
     */
    public BankAccount getDestinationAccount() {
        return destinationAccount;
    }

    /**
     * Returns the amount.
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the created at.
     * @return the created at
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the status.
     * @return the status
     */
    public TransferStatus getStatus() {
        return status;
    }

    /**
     * Marks this new transfer completed within the balance update transaction.
     * Persistence becomes visible only after the transaction commits.
     */
    public void markCompleted() {
        if (status != TransferStatus.PENDING) {
            throw new IllegalStateException("Transfer is not pending");
        }
        status = TransferStatus.COMPLETED;
    }
}
