package com.tdea.laboratoriobanco.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Persistent bank customer. */
@Entity
@Table(name = "bank_customers")
public class BankCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    protected BankCustomer() {
        // Required by JPA.
    }

    /**
     * Creates a bank customer.
     * @param fullName the full name
     * @param email the email
     */
    public BankCustomer(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    /**
     * Returns the id.
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the full name.
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }
}
