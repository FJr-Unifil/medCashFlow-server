package example.medCashFlow.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({ResourceNotFoundException.class, DisabledException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ApiError handleResourceNotFound(RuntimeException ex) {
        log.info("Resource not found: {}", ex.getMessage());

        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title(determineNotFoundTitle(ex))
                .description(ex.getMessage())
                .technicalDetails(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();
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
                .status(HttpStatus.CONFLICT.value())
                .title(determineInvalidDataTitle(ex))
                .description(ex.getMessage())
                .technicalDetails(ex.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    private String determineInvalidDataTitle(RuntimeException ex) {
        if (ex instanceof InvalidClinicException) return "Clínica Não Encontrada";
        if (ex instanceof InvalidEmployeeException) return "Funcionário Não Encontrado";
        if (ex instanceof InvalidInvolvedException) return "Envolvido não Encontrado";
        return "Recurso Não Encontrado";
    }

}
