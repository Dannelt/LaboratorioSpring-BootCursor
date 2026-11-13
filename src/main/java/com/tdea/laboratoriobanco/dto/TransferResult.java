package com.tdea.laboratoriobanco.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import com.tdea.laboratoriobanco.entity.TransferStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Transfer receipt data, without exposing managed entities.
 * @param reference unique transfer reference
 * @param sourceAccountNumber source account number
 * @param destinationAccountNumber destination account number
 * @param amount transferred amount
 * @param createdAt registration time
 * @param status transfer status
 */
@Schema(description = "Transfer receipt with its unique UUID, amount, timestamp and status")
public record TransferResult(UUID reference, String sourceAccountNumber,
        String destinationAccountNumber, BigDecimal amount, Instant createdAt,
        TransferStatus status) {
}
