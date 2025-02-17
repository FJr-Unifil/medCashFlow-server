package example.medCashFlow.exceptions;

public class ClinicNotFoundException extends ResourceNotFoundException {

    public ClinicNotFoundException(String identifier) {
        super("Clínica", identifier);
    }

    public ClinicNotFoundException() {
        super("Clinica não encontrada");
    }
}
