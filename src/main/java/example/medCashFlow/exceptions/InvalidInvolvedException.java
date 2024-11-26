package example.medCashFlow.exceptions;

public class InvalidInvolvedException extends RuntimeException {
    public InvalidInvolvedException(String message) {
        super(message);
    }

    public InvalidInvolvedException() {
        super("Envolvido inválido");
    }
}
