package example.medCashFlow.exceptions;

public class InstallmentNotFoundException extends ResourceNotFoundException {

    public InstallmentNotFoundException(String identifier) {
        super("Parcela", identifier);
    }

    public InstallmentNotFoundException() {
        super("Parcela não encontrada");
    }
}
