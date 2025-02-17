package example.medCashFlow.exceptions;

public class RoleNotFoundException extends ResourceNotFoundException {

    public RoleNotFoundException(String identifier) {
        super("Cargo", identifier);
    }

    public RoleNotFoundException() {
        super("Cargo não encontrado");
    }
}
