package example.medCashFlow.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated Data")
public abstract class InvalidDataException extends RuntimeException {

  public InvalidDataException(String resourceName, String reason) {
    super(String.format("%s inválido(a): %s", resourceName, reason));
  }

  public InvalidDataException(String message) {
    super(message);
  }
}
