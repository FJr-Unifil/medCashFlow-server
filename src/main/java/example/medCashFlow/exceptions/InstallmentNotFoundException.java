package example.medCashFlow.exceptions;

public class InstallmentNotFoundException extends RuntimeException {
    public InstallmentNotFoundException(String message) {
        super(message);
    }

    public InstallmentNotFoundException() {
        super("Parcela não encontrada");
    }
}
