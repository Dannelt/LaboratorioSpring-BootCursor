package com.tdea.laboratoriobanco.controller;

import java.util.List;
import jakarta.validation.constraints.Positive;
import com.tdea.laboratoriobanco.service.BankAccountService;
import com.tdea.laboratoriobanco.service.BankTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import com.tdea.laboratoriobanco.dto.*;
import org.springframework.web.bind.annotation.*;

/** Account query endpoints; balances can only change through transfers. */
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Account snapshots and transfer history")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected internal error",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
})
public class BankAccountController {
    private final BankAccountService accounts;
    private final BankTransferService transfers;

    /**
     * Creates the account controller.
     * @param accounts account service
     * @param transfers transfer service
     */
    public BankAccountController(BankAccountService accounts, BankTransferService transfers) {
        this.accounts = accounts;
        this.transfers = transfers;
    }

    /**
     * Lists available accounts.
     * @return account snapshots
     */
    @GetMapping
    @Operation(summary = "List accounts for source and destination selection")
    @ApiResponse(responseCode = "200", description = "Available accounts")
    public List<AccountResult> listAccounts() {
        return accounts.listAccounts();
    }

    /**
     * Reads an account.
     * @param id account identifier
     * @return account snapshot and balance
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an account and its balance")
    @ApiResponse(responseCode = "200", description = "Account found")
    public AccountResult getAccount(@Parameter(description = "Account identifier", example = "1")
            @PathVariable @Positive Long id) {
        return accounts.getAccount(id);
    }

    /**
     * Reads incoming and outgoing history.
     * @param id account identifier
     * @return transfers, newest first
     */
    @GetMapping("/{id}/transfers")
    @Operation(summary = "Get sent and received transfers, newest first")
    @ApiResponse(responseCode = "200", description = "Transfer history, possibly empty")
    public List<TransferResult> getTransfers(
            @Parameter(description = "Account identifier", example = "1") @PathVariable @Positive Long id) {
        return transfers.getAccountTransfers(id);
    }
}
