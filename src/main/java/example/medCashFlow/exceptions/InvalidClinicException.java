package example.medCashFlow.exceptions;

public class InvalidClinicException extends RuntimeException{

    public InvalidClinicException() {
        super("Clínica Inválida");
    }

    public InvalidClinicException(String message) {
        super(message);
    }
}
