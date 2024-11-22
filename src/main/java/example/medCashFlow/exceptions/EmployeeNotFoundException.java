package example.medCashFlow.exceptions;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException() {
        super("Funcionário não encontrado");
    }

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
