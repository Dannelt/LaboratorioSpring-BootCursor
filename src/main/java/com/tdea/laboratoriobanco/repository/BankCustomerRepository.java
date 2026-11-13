package com.tdea.laboratoriobanco.repository;

import com.tdea.laboratoriobanco.entity.BankCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Persistence operations for BankCustomer entities. */
public interface BankCustomerRepository extends JpaRepository<BankCustomer, Long> {

    /**
     * Finds a customer by exact email address.
     * @param email the email address
     * @return the matching customer, if present
     */
    Optional<BankCustomer> findByEmail(String email);
}
