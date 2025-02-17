package example.medCashFlow.exceptions;

public class InvalidEmployeeException extends InvalidDataException {

    public InvalidEmployeeException(String identifier) {
        super(identifier);
    }

    public InvalidEmployeeException() {
        super("Funcionário Inválido");
    }
}
