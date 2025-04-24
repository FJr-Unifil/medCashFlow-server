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
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ApiError> handleSecurityException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ApiError error = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .title("Erro interno do servidor")
                .description("Um erro inesperado ocorreu")
                .debugMessage(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    private ResponseEntity<ApiError> handleBadCredentialsException(Exception ex) {
        log.warn("Authentication failed", ex);

        ApiError error = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .title("Senha/Login inválido")
                .description("Verifique seu email ou senha")
                .debugMessage(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({AccessDeniedException.class, ForbiddenException.class})
    private ResponseEntity<ApiError> handleAuthorizationException(RuntimeException ex) {
        log.info("Acesso Negado", ex);

        ApiError error = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .title("Acesso Negado")
                .description(ex.getLocalizedMessage())
                .debugMessage(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({ResourceNotFoundException.class, DisabledException.class})
    private ResponseEntity<ApiError> handleResourceNotFound(RuntimeException ex) {
        log.info("Resource not found: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .title(ex.getLocalizedMessage())
                .description(ex.getMessage())
                .debugMessage(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);     
    }

    @ExceptionHandler(InvalidDataException.class)
    private ResponseEntity<ApiError> handleInvalidData(InvalidDataException ex) {
        log.info("Invalid Data: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .title("CONFLICT")
                .description(ex.getLocalizedMessage())
                .debugMessage(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

}
