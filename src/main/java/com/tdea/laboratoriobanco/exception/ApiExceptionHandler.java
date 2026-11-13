package com.tdea.laboratoriobanco.exception;

import java.time.Instant;
import com.tdea.laboratoriobanco.dto.ApiError;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Central HTTP translation for business, validation and unexpected errors. */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles absent banking resources.
     * @param exception missing resource error
     * @param request current request
     * @return safe 404 response
     */
    @ExceptionHandler({AccountNotFoundException.class, TransferNotFoundException.class})
    public ResponseEntity<Object> notFound(RuntimeException exception, WebRequest request) {
        return error(HttpStatus.NOT_FOUND, exception instanceof AccountNotFoundException
                ? "Account not found" : "Transfer not found", new HttpHeaders(), request);
    }

    /**
     * Handles invalid business input.
     * @param exception invalid input error
     * @param request current request
     * @return safe 400 response
     */
    @ExceptionHandler({InvalidAmountException.class, SameAccountTransferException.class})
    public ResponseEntity<Object> invalid(RuntimeException exception, WebRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception instanceof SameAccountTransferException
                ? "Source and destination accounts must differ"
                : "Amount must be positive and fit numeric(19,2)", new HttpHeaders(), request);
    }

    /**
     * Handles insufficient funds and persistence conflicts.
     * @param exception business or persistence conflict
     * @param request current request
     * @return safe 409 response
     */
    @ExceptionHandler({InsufficientBalanceException.class, TransferConcurrencyException.class,
            ConcurrencyFailureException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Object> conflict(RuntimeException exception, WebRequest request) {
        return error(HttpStatus.CONFLICT, exception instanceof InsufficientBalanceException
                ? "Insufficient balance" : "Operation conflicts with the current account state",
                new HttpHeaders(), request);
    }

    /**
     * Hides unexpected implementation details from clients.
     * @param exception unexpected failure
     * @param request current request
     * @return generic 500 response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> unexpected(Exception exception, WebRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal error",
                new HttpHeaders(), request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpStatus knownStatus = HttpStatus.resolve(status.value());
        String message = status.value() == 400 ? "Invalid request body or parameter"
                : knownStatus == null ? "Request failed" : knownStatus.getReasonPhrase();
        return error(status, message, headers, request);
    }

    private ResponseEntity<Object> error(HttpStatusCode status, String message,
            HttpHeaders headers, WebRequest request) {
        String path = request.getDescription(false).replaceFirst("^uri=", "");
        return new ResponseEntity<>(new ApiError(status.value(), message, path, Instant.now()),
                headers, status);
    }
}
