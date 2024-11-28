package example.medCashFlow.exceptions;

public class BillNotFoundException extends RuntimeException {
    public BillNotFoundException() {
        super("Conta não encontrada");
    }

    public BillNotFoundException(String message) {
        super(message);
    }
}
