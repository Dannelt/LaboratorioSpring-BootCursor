package com.tdea.laboratoriobanco.service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;
import com.tdea.laboratoriobanco.dto.TransferRequest;
import com.tdea.laboratoriobanco.entity.*;
import com.tdea.laboratoriobanco.exception.InsufficientBalanceException;
import com.tdea.laboratoriobanco.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Opt-in PostgreSQL tests: set RUN_POSTGRES_TESTS=true and DB_* locally.
 * Uses the existing laboratory schema (validate only) and deletes only its own fixtures.
 */
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.show-sql=false",
        "spring.datasource.hikari.connection-init-sql=SET lock_timeout TO '5s'"
})
@EnabledIfEnvironmentVariable(named = "RUN_POSTGRES_TESTS", matches = "true")
@EnabledIfEnvironmentVariable(named = "DB_URL",
        matches = "jdbc:postgresql://localhost:5432/laboratoriobanco")
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class BankTransferPostgresTest {
    @Autowired BankTransferService service;
    @Autowired BankAccountRepository accounts;
    @Autowired BankCustomerRepository customers;
    @Autowired BankTransferRepository transfers;
    @Autowired PlatformTransactionManager manager;
    private Long customerId;
    private Long sourceId;
    private Long destinationId;
    private TransactionTemplate transaction;

    @BeforeEach
    void createOwnedFixtures() {
        transaction = new TransactionTemplate(manager);
        transaction.executeWithoutResult(status -> {
            String token = UUID.randomUUID().toString();
            var customer = customers.saveAndFlush(new BankCustomer("Integration Test",
                    token + "@example.invalid"));
            customerId = customer.getId();
            sourceId = accounts.saveAndFlush(new BankAccount("S" + token.substring(0, 32),
                    new BigDecimal("100.00"), customer)).getId();
            destinationId = accounts.saveAndFlush(new BankAccount("D" + token.substring(0, 32),
                    new BigDecimal("0.00"), customer)).getId();
        });
    }

    @AfterEach
    void removeOnlyOwnedFixtures() {
        transaction.executeWithoutResult(status -> {
            if (sourceId != null) {
                transfers.deleteAll(transfers.findTransfersByAccountId(sourceId));
                transfers.flush();
            }
            if (destinationId != null) {
                transfers.deleteAll(transfers.findTransfersByAccountId(destinationId));
                transfers.flush();
            }
            if (sourceId != null) accounts.deleteById(sourceId);
            if (destinationId != null) accounts.deleteById(destinationId);
            accounts.flush();
            if (customerId != null) customers.deleteById(customerId);
        });
    }

    @Test
    void persistsBalancesVersionAndCompletedHistory() {
        Long oldVersion = accounts.findById(sourceId).orElseThrow().getVersion();
        var result = service.transfer(request("35.00"));
        assertBalances("65.00", "35.00");
        assertTrue(accounts.findById(sourceId).orElseThrow().getVersion() > oldVersion);
        var history = transfers.findTransfersByAccountId(sourceId);
        assertEquals(1, history.size());
        assertEquals(result.reference(), history.getFirst().getReference());
        assertEquals(TransferStatus.COMPLETED, history.getFirst().getStatus());
        assertEquals(1, transfers.findTransfersByAccountId(destinationId).size());
    }

    @Test
    void failedTransferLeavesBalancesAndHistoryUnchanged() {
        assertThrows(InsufficientBalanceException.class, () -> service.transfer(request("101.00")));
        assertBalances("100.00", "0.00");
        assertTrue(transfers.findTransfersByAccountId(sourceId).isEmpty());
    }

    @Test
    void rollsBackAlreadyFlushedBalancesAndHistoryWhenOuterTransactionFails() {
        assertThrows(IllegalStateException.class, () -> transaction.executeWithoutResult(status -> {
            service.transfer(request("30.00"));
            throw new IllegalStateException("Injected failure after flush");
        }));
        assertBalances("100.00", "0.00");
        assertTrue(transfers.findTransfersByAccountId(sourceId).isEmpty());
    }

    @Test
    @Timeout(30)
    void concurrentTransfersCannotSpendTheSameBalance() throws Exception {
        var ready = new CountDownLatch(2);
        var start = new CountDownLatch(1);
        Callable<Boolean> attempt = () -> {
            ready.countDown();
            if (!start.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Start timeout");
            try {
                service.transfer(request("80.00"));
                return true;
            } catch (InsufficientBalanceException expected) {
                return false;
            }
        };
        try (var executor = Executors.newFixedThreadPool(2)) {
            Future<Boolean> first = executor.submit(attempt);
            Future<Boolean> second = executor.submit(attempt);
            try {
                assertTrue(ready.await(5, TimeUnit.SECONDS));
            } finally {
                start.countDown();
            }
            int successes = (first.get(15, TimeUnit.SECONDS) ? 1 : 0)
                    + (second.get(15, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, successes);
        }
        assertBalances("20.00", "80.00");
        assertEquals(1, transfers.findTransfersByAccountId(sourceId).size());
    }

    private TransferRequest request(String amount) {
        return new TransferRequest(sourceId, destinationId, new BigDecimal(amount));
    }

    private void assertBalances(String source, String destination) {
        BigDecimal sourceBalance = accounts.findById(sourceId).orElseThrow().getBalance();
        BigDecimal destinationBalance = accounts.findById(destinationId).orElseThrow().getBalance();
        assertEquals(new BigDecimal(source), sourceBalance);
        assertEquals(new BigDecimal(destination), destinationBalance);
        assertEquals(new BigDecimal("100.00"), sourceBalance.add(destinationBalance));
    }
}
