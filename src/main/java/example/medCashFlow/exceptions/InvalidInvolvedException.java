package example.medCashFlow.exceptions;

public class InvalidInvolvedException extends InvalidDataException {

    public InvalidInvolvedException(String identifier) {
        super(identifier);
    }

    public InvalidInvolvedException() {
        super("Envolvido inválido");
    }
}
