package example.medCashFlow.exceptions;

public class EmployeeNotFoundException extends ResourceNotFoundException {

    public EmployeeNotFoundException(String identifier) {
        super("Funcionário", identifier);
    }

    public EmployeeNotFoundException() {
        super("Funcionário não encontrado");
    }
}
