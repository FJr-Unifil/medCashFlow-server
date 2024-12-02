package example.medCashFlow.exceptions;

public class InvolvedNotFoundException extends RuntimeException {
    public InvolvedNotFoundException(String message) {
        super(message);
    }

    public InvolvedNotFoundException() {
        super("Envolvido não encontrado");
    }
}
