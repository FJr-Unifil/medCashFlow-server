package example.medCashFlow.exceptions;

public class InvalidClinicException extends InvalidDataException{

    public InvalidClinicException(String identifier) {
        super(identifier);
    }

    public InvalidClinicException() {
        super("Clínica Inválida");
    }
}
