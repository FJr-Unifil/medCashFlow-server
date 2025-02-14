package example.medCashFlow.exceptions;

import example.medCashFlow.dto.ExceptionDTO;
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
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Erro interno do servidor")
                .description("Um erro inesperado ocorreu")
                .technicalDetails(ex.getClass().getName() + ": " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    private ResponseEntity<ApiError> handleBadCredentialsException(Exception ex) {
        log.warn("Authentication failed", ex);

        ApiError error = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .title("Senha/Login inválido")
                .description("Verifique seu email ou senha")
                .technicalDetails(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AccessDeniedException.class, ForbiddenException.class})
    private ResponseEntity<ApiError> handleAuthorizationException(RuntimeException ex) {
        log.info("Acesso Negado", ex);

        ApiError error = ApiError.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .title("Acesso Negado")
                .description(ex.getMessage())
                .technicalDetails(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ResourceNotFoundException.class, DisabledException.class})
    protected ResponseEntity<ApiError> handleResourceNotFound(RuntimeException ex) {
        log.info("Resource not found: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title(determineNotFoundTitle(ex))
                .description(ex.getMessage())
                .technicalDetails(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    private String determineNotFoundTitle(RuntimeException ex) {
        if (ex instanceof ClinicNotFoundException) return "Clínica Não Encontrada";
        if (ex instanceof EmployeeNotFoundException) return "Funcionário Não Encontrado";
        if (ex instanceof InvolvedNotFoundException) return "Envolvido não Encontrado";
        if (ex instanceof AccountPlanningNotFoundException) return "Plano de Contas Não Encontrado";
        if (ex instanceof BillNotFoundException) return "Conta Não Encontrada";
        return "Recurso Não Encontrado";
    }

    @ExceptionHandler(InvalidDataException.class)
    protected ResponseEntity<ApiError> handleInvalidData(InvalidDataException ex) {
        log.info("Invalid Data: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title(determineInvalidDataTitle(ex))
                .description(ex.getMessage())
                .technicalDetails(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    private String determineInvalidDataTitle(RuntimeException ex) {
        if (ex instanceof InvalidClinicException) return "Clínica Não Encontrada";
        if (ex instanceof InvalidEmployeeException) return "Funcionário Não Encontrado";
        if (ex instanceof InvalidInvolvedException) return "Envolvido não Encontrado";
        return "Recurso Não Encontrado";
    }

}
