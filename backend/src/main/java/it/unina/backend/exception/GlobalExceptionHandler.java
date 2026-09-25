package it.unina.backend.exception;

import org.jooq.exception.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.postgresql.util.PSQLException;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleJooqException(DataAccessException ex) {
        if (ex.getCause() instanceof PSQLException psqlEx) {
            if ("P0001".equals(psqlEx.getSQLState())) {
                ErrorResponse error = new ErrorResponse(
                        psqlEx.getServerErrorMessage().getMessage(),
                        OffsetDateTime.now()
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }

        ErrorResponse genericError = new ErrorResponse(
                "An unexpected database error occurred.\n" +
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericError);
    }
}