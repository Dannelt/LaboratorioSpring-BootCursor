package com.tdea.laboratoriobanco.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Account snapshot without a JPA entity.
 * @param id account identifier
 * @param accountNumber unique account number
 * @param balance current balance
 */
@Schema(description = "Account selection data and current balance")
public record AccountResult(
        @Schema(description = "Account identifier", example = "1") Long id,
        @Schema(description = "Unique account number", example = "LAB-DEMO-001") String accountNumber,
        @Schema(description = "Current balance", example = "1000.00") BigDecimal balance) {
}
