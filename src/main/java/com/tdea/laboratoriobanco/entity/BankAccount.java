package com.tdea.laboratoriobanco.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

/** Persistent bank account. */
@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 34)
    @Column(name = "account_number", nullable = false, unique = true, length = 34)
    private String accountNumber;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 17, fraction = 2)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bank_accounts_customer"))
    private BankCustomer customer;

    @Version
    @Column(nullable = false)
    private Long version;

    protected BankAccount() {
        // Required by JPA.
    }

    /**
     * Creates a bank account.
     * @param accountNumber the account number
     * @param balance the balance
     * @param customer the customer
     */
    public BankAccount(String accountNumber, BigDecimal balance, BankCustomer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.customer = customer;
    }

    /**
     * Returns the id.
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the account number.
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Returns the balance.
     * @return the balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Changes the balance of a managed account inside a locked transaction.
     * @param balance nonnegative balance representable as numeric(19,2)
     */
    public void changeBalance(BigDecimal balance) {
        if (balance == null || balance.signum() < 0 || balance.scale() > 2
                || balance.compareTo(new BigDecimal("99999999999999999.99")) > 0) {
            throw new IllegalArgumentException("Invalid account balance");
        }
        this.balance = balance;
    }

    /**
     * Returns the customer.
     * @return the customer
     */
    public BankCustomer getCustomer() {
        return customer;
    }

    /**
     * Returns the version.
     * @return the version
     */
    public Long getVersion() {
        return version;
    }
}
