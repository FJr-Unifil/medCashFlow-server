package example.medCashFlow.exceptions;

public class ClinicNotFoundException extends RuntimeException {

    public ClinicNotFoundException() {
        super("Clinica não encontrada");
    }

    public ClinicNotFoundException(String message) {
        super(message);
    }
}
