package com.tdea.laboratoriobanco.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Requested transfer, with monetary precision matching the database.
 * @param sourceAccountId source account identifier
 * @param destinationAccountId destination account identifier
 * @param amount strictly positive amount with at most two decimal places
 */
@Schema(description = "Transfer input; different accounts and sufficient funds are required")
public record TransferRequest(
        @Schema(description = "Source account ID", example = "1")
        @NotNull @Positive Long sourceAccountId,
        @Schema(description = "Destination account ID", example = "2")
        @NotNull @Positive Long destinationAccountId,
        @Schema(description = "Positive amount; at most 17 integer and 2 fractional digits", example = "25.00")
        @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal amount) {
}
