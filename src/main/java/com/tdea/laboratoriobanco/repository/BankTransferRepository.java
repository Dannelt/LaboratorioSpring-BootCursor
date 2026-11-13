package com.tdea.laboratoriobanco.repository;

import com.tdea.laboratoriobanco.entity.BankTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for BankTransfer entities. */
public interface BankTransferRepository extends JpaRepository<BankTransfer, Long> {

    /**
     * Finds a transfer by its receipt reference.
     * @param reference unique reference
     * @return transfer, if present
     */
    Optional<BankTransfer> findByReference(UUID reference);

    /**
     * Finds incoming and outgoing transfers, newest first.
     * @param accountId account identifier
     * @return transfers ordered by date and ID descending
     */
    @Query("""
            select transfer from BankTransfer transfer
            join fetch transfer.sourceAccount
            join fetch transfer.destinationAccount
            where transfer.sourceAccount.id = :accountId
               or transfer.destinationAccount.id = :accountId
            order by transfer.createdAt desc, transfer.id desc
            """)
    List<BankTransfer> findRecentTransfersByAccountId(@Param("accountId") Long accountId);

    /**
     * Finds sent and received transfers, oldest first, using the ID to break ties.
     * @param accountId the account identifier
     * @return transfers involving the account
     */
    @Query("""
            select transfer from BankTransfer transfer
            where transfer.sourceAccount.id = :accountId
               or transfer.destinationAccount.id = :accountId
            order by transfer.createdAt asc, transfer.id asc
            """)
    List<BankTransfer> findTransfersByAccountId(@Param("accountId") Long accountId);
}
