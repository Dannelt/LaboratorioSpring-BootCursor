package com.tdea.laboratoriobanco.controller;

import java.net.URI;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.tdea.laboratoriobanco.service.BankTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import com.tdea.laboratoriobanco.dto.*;
import org.springframework.web.bind.annotation.*;

/** Transfer creation and receipt lookup endpoints. */
@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfers", description = "Atomic transfers and receipt lookup")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected internal error",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
})
public class BankTransferController {
    private final BankTransferService transfers;

    /**
     * Creates the transfer controller.
     * @param transfers transfer service
     */
    public BankTransferController(BankTransferService transfers) {
        this.transfers = transfers;
    }

    /**
     * Performs a transfer through the transactional service.
     * @param request validated transfer input
     * @return created receipt and its Location header
     */
    @PostMapping
    @Operation(summary = "Transfer money atomically between different accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transfer committed",
                    content = @Content(schema = @Schema(implementation = TransferResult.class))),
            @ApiResponse(responseCode = "409", description = "Insufficient balance or concurrency conflict",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<TransferResult> transfer(@Valid @RequestBody TransferRequest request) {
        TransferResult result = transfers.transfer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{reference}")
                .buildAndExpand(result.reference()).toUri();
        return ResponseEntity.created(location).body(result);
    }

    /**
     * Retrieves a transfer receipt.
     * @param reference transfer UUID
     * @return persisted receipt data
     */
    @GetMapping("/{reference}")
    @Operation(summary = "Get a transfer receipt by UUID")
    @ApiResponse(responseCode = "200", description = "Transfer found")
    public TransferResult getTransfer(@Parameter(description = "Transfer UUID")
            @PathVariable UUID reference) {
        return transfers.getTransfer(reference);
    }
}
