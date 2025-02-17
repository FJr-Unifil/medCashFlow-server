package example.medCashFlow.exceptions;

public class InvolvedNotFoundException extends ResourceNotFoundException {

    public InvolvedNotFoundException(String identifier) {
        super("Envolvido", identifier);
    }

    public InvolvedNotFoundException() {
        super("Envolvido não encontrado");
    }
}
