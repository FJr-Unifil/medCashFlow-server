package example.medCashFlow.exceptions;

import example.medCashFlow.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ExceptionDTO> handleSecurityException() {
        return ResponseEntity.status(500).body(new ExceptionDTO(500,
                "Erro interno do servidor",
                "Ocorreu um erro inesperado",
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    private ResponseEntity<ExceptionDTO> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionDTO(
                401,
                "Senha/Login inválido",
                "Verifique seu email ou senha",
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    private ResponseEntity<ExceptionDTO> handleAuthorizationException() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionDTO(
                403,
                "Não Autorizado",
                "Você não tem permissão para acessar essa parte do sistema",
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(InvalidClinicException.class)
    private ResponseEntity<ExceptionDTO> handleInvalidClinicException(InvalidClinicException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDTO(
                409,
                "Conflito de Dados",
                ex.getMessage(),
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(InvalidEmployeeException.class)
    private ResponseEntity<ExceptionDTO> handleInvalidEmployeeException(InvalidEmployeeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDTO(
                409,
                "Conflito de Dados",
                ex.getMessage(),
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(ClinicNotFoundException.class)
    private ResponseEntity<ExceptionDTO> handleClinicNotFoundException(ClinicNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO(
                404,
                "Clínica Não Encontrada",
                ex.getMessage(),
                LocalDateTime.now()
        ));
    }
}
