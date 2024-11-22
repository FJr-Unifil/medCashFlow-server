package example.medCashFlow.exceptions;

public class InvalidEmployeeException extends RuntimeException {

    public InvalidEmployeeException() {
        super("Funcionário Inválido");
    }

    public InvalidEmployeeException(String message) {
        super(message);
    }
}
