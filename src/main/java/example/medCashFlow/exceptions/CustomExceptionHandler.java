package example.medCashFlow.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ApiError> handleSecurityException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ApiError error = ApiErrorBuilder.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Unexpected error occurred")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    private ResponseEntity<ApiError> handleBadCredentialsException(Exception ex) {
        log.warn("Authentication failed", ex);

        ApiError error = ApiErrorBuilder.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("Invalid login/password")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({AccessDeniedException.class, ForbiddenException.class})
    private ResponseEntity<ApiError> handleAuthorizationException(RuntimeException ex) {
        log.info("Forbidden Access", ex);

        ApiError error = ApiErrorBuilder.builder()
                .status(HttpStatus.FORBIDDEN)
                .message("Forbidden Access")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({DisabledException.class})
    private ResponseEntity<ApiError> handleDisabledException(DisabledException ex) {
        log.warn("Entity is disabled", ex);

        ApiError error = ApiErrorBuilder.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    private ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        log.info("Resource not found: {}", ex.getMessage());

        ApiValidationError validationError = new ApiValidationError(
                ex.getEntity(),
                ex.getPropertyName(),
                ex.getValue()
        );

        ApiError error = ApiErrorBuilder.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .subErrors(List.of(validationError))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);     
    }

    @ExceptionHandler({InvalidDataException.class, MultipleInvalidDataException.class})
    private ResponseEntity<ApiError> handleInvalidData(Exception ex) {
        log.info("Invalid Data: {}", ex.getMessage());

        List<ApiValidationError> validationErrors = new ArrayList<>();
        String message = "Data Conflict";

        ApiError error = null;
        if (ex instanceof InvalidDataException ide) {
            validationErrors.add(new ApiValidationError(
                    ide.getEntity(),
                    ide.getPropertyName(),
                    ide.getValue()
            ));
            error = ApiErrorBuilder.builder()
                    .status(HttpStatus.CONFLICT)
                    .message(message)
                    .subErrors(validationErrors)
                    .build();
        } else if (ex instanceof MultipleInvalidDataException mide) {
            validationErrors.addAll(mide.getErrors());
            error = ApiErrorBuilder.builder()
                    .status(HttpStatus.CONFLICT)
                    .message("Data Conflict")
                    .subErrors(validationErrors)
                    .build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

}
