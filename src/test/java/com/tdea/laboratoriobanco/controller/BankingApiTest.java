package com.tdea.laboratoriobanco.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.tdea.laboratoriobanco.dto.*;
import com.tdea.laboratoriobanco.entity.TransferStatus;
import com.tdea.laboratoriobanco.exception.*;
import com.tdea.laboratoriobanco.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Exercises real MVC routing, JSON, validation, advice and Springdoc.
 * Business services are mocked: these HTTP tests intentionally need no database.
 * Existing context and PostgreSQL tests are left unchanged.
 */
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class BankingApiTest {
    @Autowired MockMvc mvc;
    @MockitoBean BankAccountService accounts;
    @MockitoBean BankTransferService transfers;
    private TransferResult receipt;

    @BeforeEach
    void setUp() {
        receipt = new TransferResult(UUID.randomUUID(), "LAB-DEMO-001", "LAB-DEMO-002",
                new BigDecimal("25.00"), Instant.parse("2026-01-01T10:00:00Z"),
                TransferStatus.COMPLETED);
    }

    @Test
    void listsAccountsWithoutExposingEntities() throws Exception {
        when(accounts.listAccounts()).thenReturn(List.of(
                new AccountResult(1L, "LAB-DEMO-001", new BigDecimal("1000.00"))));
        mvc.perform(get("/api/accounts")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].balance").value(1000))
                .andExpect(jsonPath("$[0].customer").doesNotExist())
                .andExpect(jsonPath("$[0].version").doesNotExist());
    }

    @Test
    void returnsEmptyAccountList() throws Exception {
        when(accounts.listAccounts()).thenReturn(List.of());
        mvc.perform(get("/api/accounts")).andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getsAccount() throws Exception {
        when(accounts.getAccount(1L)).thenReturn(
                new AccountResult(1L, "LAB-DEMO-001", new BigDecimal("1000.00")));
        mvc.perform(get("/api/accounts/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("LAB-DEMO-001"));
    }

    @Test
    void missingAccountReturns404() throws Exception {
        when(accounts.getAccount(99L)).thenThrow(new AccountNotFoundException());
        mvc.perform(get("/api/accounts/99")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/accounts/99"));
    }

    @Test
    void createsTransferWithReceiptLocation() throws Exception {
        when(transfers.transfer(any())).thenReturn(receipt);
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content(body("25.00"))).andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "http://localhost/api/transfers/" + receipt.reference()))
                .andExpect(jsonPath("$.reference").value(receipt.reference().toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
        verify(transfers).transfer(new TransferRequest(1L, 2L, new BigDecimal("25.00")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.001", "null", "100000000000000000.00"})
    void rejectsInvalidAmountsBeforeCallingService(String amount) throws Exception {
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content(body(amount))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(transfers);
    }

    @Test
    void rejectsMissingFields() throws Exception {
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isBadRequest());
        verifyNoInteractions(transfers);
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content("{")).andExpect(status().isBadRequest());
        verifyNoInteractions(transfers);
    }

    @Test
    void rejectsMalformedIdentifiers() throws Exception {
        mvc.perform(get("/api/accounts/not-a-number")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/transfers/not-a-uuid")).andExpect(status().isBadRequest());
    }

    @Test
    void rejectsNonpositiveAccountIdentifiers() throws Exception {
        mvc.perform(get("/api/accounts/0")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/accounts/-1/transfers")).andExpect(status().isBadRequest());
        verifyNoInteractions(accounts, transfers);
    }

    @Test
    void insufficientBalanceReturns409() throws Exception {
        when(transfers.transfer(any())).thenThrow(new InsufficientBalanceException());
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content(body("25"))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Insufficient balance"));
    }

    @Test
    void sameAccountReturns400() throws Exception {
        when(transfers.transfer(any())).thenThrow(new SameAccountTransferException());
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content("{\"sourceAccountId\":1,\"destinationAccountId\":1,\"amount\":25}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingTransferAccountReturns404() throws Exception {
        when(transfers.transfer(any())).thenThrow(new AccountNotFoundException());
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content(body("25"))).andExpect(status().isNotFound());
    }

    @Test
    void serviceInvalidAmountReturns400() throws Exception {
        when(transfers.transfer(any())).thenThrow(new InvalidAmountException());
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content(body("25"))).andExpect(status().isBadRequest());
    }

    @Test
    void concurrencyConflictReturns409WithoutDetails() throws Exception {
        when(transfers.transfer(any())).thenThrow(
                new TransferConcurrencyException(new IllegalStateException("private details")));
        mvc.perform(post("/api/transfers").contentType(MediaType.APPLICATION_JSON)
                .content(body("25"))).andExpect(status().isConflict())
                .andExpect(content().string(not(containsString("private details"))));
    }

    @Test
    void retrievesReceiptByUuid() throws Exception {
        when(transfers.getTransfer(receipt.reference())).thenReturn(receipt);
        mvc.perform(get("/api/transfers/" + receipt.reference())).andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(25))
                .andExpect(jsonPath("$.createdAt").value("2026-01-01T10:00:00Z"));
    }

    @Test
    void missingReceiptReturns404() throws Exception {
        when(transfers.getTransfer(any())).thenThrow(new TransferNotFoundException());
        mvc.perform(get("/api/transfers/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void listsIncomingAndOutgoingHistoryNewestFirst() throws Exception {
        var olderIncoming = new TransferResult(UUID.randomUUID(), "LAB-DEMO-002", "LAB-DEMO-001",
                BigDecimal.ONE, receipt.createdAt().minusSeconds(60), TransferStatus.COMPLETED);
        when(transfers.getAccountTransfers(1L)).thenReturn(List.of(receipt, olderIncoming));
        mvc.perform(get("/api/accounts/1/transfers")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].reference").value(receipt.reference().toString()))
                .andExpect(jsonPath("$[1].destinationAccountNumber").value("LAB-DEMO-001"));
    }

    @Test
    void missingHistoryAccountReturns404() throws Exception {
        when(transfers.getAccountTransfers(99L)).thenThrow(new AccountNotFoundException());
        mvc.perform(get("/api/accounts/99/transfers")).andExpect(status().isNotFound());
    }

    @Test
    void hidesUnexpectedErrors() throws Exception {
        when(accounts.listAccounts()).thenThrow(
                new IllegalStateException("jdbc:private password=hidden stack trace"));
        mvc.perform(get("/api/accounts")).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected internal error"))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(content().string(not(containsString("password"))));
    }

    @Test
    void preserves405ForUnsupportedMethods() throws Exception {
        mvc.perform(delete("/api/accounts/1")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void servesOpenApiWithAllOperationsAndSchemas() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Bank Transfer Laboratory API"))
                .andExpect(jsonPath("$.paths['/api/accounts'].get").exists())
                .andExpect(jsonPath("$.paths['/api/accounts/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/accounts/{id}/transfers'].get").exists())
                .andExpect(jsonPath("$.paths['/api/transfers/{reference}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/transfers'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/api/transfers'].post.responses['409']").exists())
                .andExpect(jsonPath("$.components.schemas.TransferRequest").exists())
                .andExpect(jsonPath("$.components.schemas.TransferResult").exists())
                .andExpect(jsonPath("$.components.schemas.ApiError").exists());
    }

    @Test
    void servesSwaggerUiAndConfiguration() throws Exception {
        mvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/swagger-ui/index.html")));
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Swagger UI")));
        mvc.perform(get("/v3/api-docs/swagger-config")).andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("/v3/api-docs"));
    }

    private String body(String amount) {
        return "{\"sourceAccountId\":1,\"destinationAccountId\":2,\"amount\":" + amount + "}";
    }
}
