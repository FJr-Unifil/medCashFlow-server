package example.medCashFlow.exceptions;

public abstract class InvalidDataException extends MedCashFlowException {

  public InvalidDataException(String resourceName, String reason) {
    super(String.format("%s inválido(a): %s", resourceName, reason));
  }

  public InvalidDataException(String message) {
    super(message);
  }
}
