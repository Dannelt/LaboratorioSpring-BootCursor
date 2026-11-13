package com.tdea.laboratoriobanco.service;

import java.math.BigDecimal;
import java.util.Optional;
import com.tdea.laboratoriobanco.dto.TransferRequest;
import com.tdea.laboratoriobanco.entity.*;
import com.tdea.laboratoriobanco.exception.*;
import com.tdea.laboratoriobanco.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.CannotAcquireLockException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankTransferServiceTest {
    private BankAccountRepository accounts;
    private BankTransferRepository transfers;
    private BankTransferService service;
    private BankAccount source;
    private BankAccount destination;

    @BeforeEach
    void setUp() {
        accounts = mock(BankAccountRepository.class);
        transfers = mock(BankTransferRepository.class);
        service = new BankTransferService(accounts, transfers);
        var customer = new BankCustomer("Unit Test", "unit@example.invalid");
        source = new BankAccount("unit-source", new BigDecimal("100.00"), customer);
        destination = new BankAccount("unit-destination", new BigDecimal("20.00"), customer);
        when(accounts.findByIdForUpdate(1L)).thenReturn(Optional.of(source));
        when(accounts.findByIdForUpdate(2L)).thenReturn(Optional.of(destination));
    }

    @Test
    void transfersMoneyAndBuildsReceipt() {
        var result = service.transfer(request("30.00"));
        assertEquals(new BigDecimal("70.00"), source.getBalance());
        assertEquals(new BigDecimal("50.00"), destination.getBalance());
        assertEquals(new BigDecimal("120.00"), source.getBalance().add(destination.getBalance()));
        assertEquals(TransferStatus.COMPLETED, result.status());
        assertNotNull(result.reference());
        assertNotNull(result.createdAt());
        assertEquals("unit-source", result.sourceAccountNumber());
        verify(transfers).saveAndFlush(argThat(t -> t.getStatus() == TransferStatus.COMPLETED
                && t.getAmount().compareTo(new BigDecimal("30")) == 0));
        var order = inOrder(accounts);
        order.verify(accounts).findByIdForUpdate(1L);
        order.verify(accounts).findByIdForUpdate(2L);
    }

    @Test
    void locksInAscendingOrderEvenForReverseTransfer() {
        service.transfer(new TransferRequest(2L, 1L, new BigDecimal("10.00")));
        var order = inOrder(accounts);
        order.verify(accounts).findByIdForUpdate(1L);
        order.verify(accounts).findByIdForUpdate(2L);
        assertEquals(new BigDecimal("110.00"), source.getBalance());
        assertEquals(new BigDecimal("10.00"), destination.getBalance());
    }

    @Test
    void rejectsInsufficientBalanceWithoutChanges() {
        assertThrows(InsufficientBalanceException.class, () -> service.transfer(request("100.01")));
        assertUnchanged();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.001", "1.000", "100000000000000000.00"})
    @NullSource
    void rejectsInvalidAmounts(String value) {
        assertThrows(InvalidAmountException.class, () -> service.transfer(
                new TransferRequest(1L, 2L, value == null ? null : new BigDecimal(value))));
        verifyNoInteractions(accounts, transfers);
    }

    @Test
    void rejectsMissingSource() {
        when(accounts.findByIdForUpdate(1L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.transfer(request("10")));
        assertUnchanged();
    }

    @Test
    void rejectsMissingDestination() {
        when(accounts.findByIdForUpdate(2L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.transfer(request("10")));
        assertUnchanged();
    }

    @Test
    void rejectsSameAccount() {
        assertThrows(SameAccountTransferException.class,
                () -> service.transfer(new TransferRequest(1L, 1L, BigDecimal.ONE)));
        verifyNoInteractions(accounts, transfers);
    }

    @Test
    void translatesLockConflict() {
        when(accounts.findByIdForUpdate(1L)).thenThrow(new CannotAcquireLockException("test"));
        assertThrows(TransferConcurrencyException.class, () -> service.transfer(request("10")));
        assertUnchanged();
    }

    @Test
    void rejectsDestinationOverflowWithoutChanges() {
        destination.changeBalance(new BigDecimal("99999999999999999.99"));
        assertThrows(InvalidAmountException.class, () -> service.transfer(request("1")));
        assertEquals(new BigDecimal("100.00"), source.getBalance());
        verifyNoInteractions(transfers);
    }

    @Test
    void propagatesPersistenceFailureForTransactionRollback() {
        when(transfers.saveAndFlush(any())).thenThrow(new IllegalStateException("test failure"));
        assertThrows(IllegalStateException.class, () -> service.transfer(request("10")));
        // Actual rollback of managed balances is verified against PostgreSQL, not mocks.
    }

    private TransferRequest request(String amount) {
        return new TransferRequest(1L, 2L, new BigDecimal(amount));
    }

    private void assertUnchanged() {
        assertEquals(new BigDecimal("100.00"), source.getBalance());
        assertEquals(new BigDecimal("20.00"), destination.getBalance());
        verifyNoInteractions(transfers);
    }
}
