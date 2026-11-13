package com.tdea.laboratoriobanco.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import com.tdea.laboratoriobanco.dto.TransferRequest;
import com.tdea.laboratoriobanco.dto.TransferResult;
import com.tdea.laboratoriobanco.entity.BankAccount;
import com.tdea.laboratoriobanco.entity.BankTransfer;
import com.tdea.laboratoriobanco.exception.*;
import com.tdea.laboratoriobanco.repository.BankAccountRepository;
import com.tdea.laboratoriobanco.repository.BankTransferRepository;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/** Atomic transfers between accounts in the same database. */
@Service
public class BankTransferService {
    private final BankAccountRepository accounts;
    private final BankTransferRepository transfers;

    /**
     * Creates the transfer service.
     * @param accounts account repository
     * @param transfers transfer repository
     */
    public BankTransferService(BankAccountRepository accounts, BankTransferRepository transfers) {
        this.accounts = accounts;
        this.transfers = transfers;
    }

    /**
     * Locks both accounts by ascending ID and transfers money atomically.
     * The Spring proxy commits before returning to a nontransactional caller.
     * When joining an existing transaction, its caller owns the final commit.
     * @param request source, destination and amount
     * @return receipt data, subject to successful transaction commit
     * @throws InvalidAmountException for a null, nonpositive or unrepresentable amount
     * @throws AccountNotFoundException for an invalid or missing account
     * @throws SameAccountTransferException when both identifiers are equal
     * @throws InsufficientBalanceException when the source cannot fund the transfer
     * @throws TransferConcurrencyException when locking or flushing detects a conflict
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public TransferResult transfer(TransferRequest request) {
        if (request == null) {
            throw new InvalidAmountException();
        }
        BigDecimal amount = monetaryAmount(request.amount());
        Long sourceId = request.sourceAccountId();
        Long destinationId = request.destinationAccountId();
        if (sourceId == null || destinationId == null || sourceId <= 0 || destinationId <= 0) {
            throw new AccountNotFoundException();
        }
        if (sourceId.equals(destinationId)) {
            throw new SameAccountTransferException();
        }
        try {
            BankAccount first = lock(Math.min(sourceId, destinationId));
            BankAccount second = lock(Math.max(sourceId, destinationId));
            BankAccount source = sourceId < destinationId ? first : second;
            BankAccount destination = sourceId < destinationId ? second : first;
            if (source.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException();
            }
            BigDecimal destinationBalance = destination.getBalance().add(amount);
            if (destinationBalance.compareTo(new BigDecimal("99999999999999999.99")) > 0) {
                throw new InvalidAmountException();
            }
            source.changeBalance(source.getBalance().subtract(amount));
            destination.changeBalance(destinationBalance);
            var transfer = new BankTransfer(source, destination, amount);
            transfer.markCompleted();
            transfers.saveAndFlush(transfer);
            return new TransferResult(transfer.getReference(), source.getAccountNumber(),
                    destination.getAccountNumber(), amount, transfer.getCreatedAt(), transfer.getStatus());
        } catch (ConcurrencyFailureException exception) {
            throw new TransferConcurrencyException(exception);
        }
    }

    /**
     * Reads receipt data for a persisted transfer.
     * @param reference transfer reference
     * @return receipt data
     * @throws TransferNotFoundException when no transfer matches
     */
    @Transactional(readOnly = true)
    public TransferResult getTransfer(UUID reference) {
        return toResult(transfers.findByReference(reference)
                .orElseThrow(TransferNotFoundException::new));
    }

    /**
     * Lists sent and received transfers, newest first.
     * @param accountId account identifier
     * @return receipt data in reverse chronological order
     * @throws AccountNotFoundException when the account does not exist
     */
    @Transactional(readOnly = true)
    public List<TransferResult> getAccountTransfers(Long accountId) {
        if (accountId == null || accountId <= 0 || !accounts.existsById(accountId)) {
            throw new AccountNotFoundException();
        }
        return transfers.findRecentTransfersByAccountId(accountId).stream()
                .map(this::toResult).toList();
    }

    private TransferResult toResult(BankTransfer transfer) {
        return new TransferResult(transfer.getReference(),
                transfer.getSourceAccount().getAccountNumber(),
                transfer.getDestinationAccount().getAccountNumber(),
                transfer.getAmount(), transfer.getCreatedAt(), transfer.getStatus());
    }

    private BankAccount lock(Long id) {
        return accounts.findByIdForUpdate(id).orElseThrow(AccountNotFoundException::new);
    }

    private BigDecimal monetaryAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2) {
            throw new InvalidAmountException();
        }
        try {
            BigDecimal normalized = amount.setScale(2, RoundingMode.UNNECESSARY);
            if (normalized.precision() > 19) {
                throw new InvalidAmountException();
            }
            return normalized;
        } catch (ArithmeticException exception) {
            throw new InvalidAmountException();
        }
    }
}
