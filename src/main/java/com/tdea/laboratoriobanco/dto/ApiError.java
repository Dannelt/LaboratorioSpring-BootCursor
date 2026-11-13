package com.tdea.laboratoriobanco.dto;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Safe HTTP error response without internal exception details.
 * @param status HTTP status code
 * @param message public error description
 * @param path request path
 * @param timestamp error time
 */
@Schema(description = "Public error response; contains no internal stack trace")
public record ApiError(int status, String message, String path, Instant timestamp) {
}
