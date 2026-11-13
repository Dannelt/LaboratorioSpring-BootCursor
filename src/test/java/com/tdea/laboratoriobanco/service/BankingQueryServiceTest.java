package com.tdea.laboratoriobanco.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.tdea.laboratoriobanco.entity.*;
import com.tdea.laboratoriobanco.exception.*;
import com.tdea.laboratoriobanco.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Tests DTO mapping and query selection inside read transactions. */
class BankingQueryServiceTest {
    private BankAccountRepository accounts;
    private BankTransferRepository transfers;
    private BankAccountService accountService;
    private BankTransferService transferService;
    private BankAccount source;
    private BankAccount destination;

    @BeforeEach
    void setUp() {
        accounts = mock(BankAccountRepository.class);
        transfers = mock(BankTransferRepository.class);
        accountService = new BankAccountService(accounts);
        transferService = new BankTransferService(accounts, transfers);
        var customer = new BankCustomer("Query Test", "query@example.invalid");
        source = new BankAccount("source", new BigDecimal("100.00"), customer);
        destination = new BankAccount("destination", BigDecimal.ZERO, customer);
    }

    @Test
    void mapsAccountListToRecords() {
        when(accounts.findAll(Sort.by("id"))).thenReturn(List.of(source, destination));
        var result = accountService.listAccounts();
        assertEquals(2, result.size());
        assertEquals("source", result.getFirst().accountNumber());
        assertEquals(new BigDecimal("100.00"), result.getFirst().balance());
    }

    @Test
    void readsReceiptInsideService() {
        var transfer = new BankTransfer(source, destination, new BigDecimal("5.00"));
        transfer.markCompleted();
        when(transfers.findByReference(transfer.getReference())).thenReturn(Optional.of(transfer));
        var receipt = transferService.getTransfer(transfer.getReference());
        assertEquals("source", receipt.sourceAccountNumber());
        assertEquals("destination", receipt.destinationAccountNumber());
        assertEquals(TransferStatus.COMPLETED, receipt.status());
    }

    @Test
    void rejectsUnknownReference() {
        UUID reference = UUID.randomUUID();
        when(transfers.findByReference(reference)).thenReturn(Optional.empty());
        assertThrows(TransferNotFoundException.class, () -> transferService.getTransfer(reference));
    }

    @Test
    void selectsNewestFirstQueryAndKeepsBothDirections() {
        var outgoing = new BankTransfer(source, destination, BigDecimal.ONE);
        var incoming = new BankTransfer(destination, source, BigDecimal.TEN);
        when(accounts.existsById(1L)).thenReturn(true);
        when(transfers.findRecentTransfersByAccountId(1L)).thenReturn(List.of(incoming, outgoing));
        var history = transferService.getAccountTransfers(1L);
        assertEquals(incoming.getReference(), history.getFirst().reference());
        assertEquals("destination", history.getFirst().sourceAccountNumber());
        assertEquals("source", history.get(1).sourceAccountNumber());
        verify(transfers).findRecentTransfersByAccountId(1L);
        verify(transfers, never()).findTransfersByAccountId(1L);
    }

    @Test
    void distinguishesMissingAccountFromEmptyHistory() {
        when(accounts.existsById(1L)).thenReturn(false);
        assertThrows(AccountNotFoundException.class, () -> transferService.getAccountTransfers(1L));
        verifyNoInteractions(transfers);
    }

    @Test
    void returnsEmptyHistoryForExistingAccount() {
        when(accounts.existsById(1L)).thenReturn(true);
        when(transfers.findRecentTransfersByAccountId(1L)).thenReturn(List.of());
        assertTrue(transferService.getAccountTransfers(1L).isEmpty());
    }
}
