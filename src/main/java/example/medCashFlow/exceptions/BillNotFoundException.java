package example.medCashFlow.exceptions;

public class BillNotFoundException extends ResourceNotFoundException {

    public BillNotFoundException(String identifier) {
        super("Conta", identifier);
    }

    public BillNotFoundException() {
        super("Conta não encontrada");
    }
}
