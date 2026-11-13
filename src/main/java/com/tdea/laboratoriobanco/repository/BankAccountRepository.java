package com.tdea.laboratoriobanco.repository;

import com.tdea.laboratoriobanco.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for BankAccount entities. */
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    /**
     * Locks an account until the surrounding transaction ends.
     * @param id account identifier
     * @return locked account, if present
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from BankAccount account where account.id = :id")
    Optional<BankAccount> findByIdForUpdate(@Param("id") Long id);

    /**
     * Finds an account by its unique number.
     * @param accountNumber the account number
     * @return the matching account, if present
     */
    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
