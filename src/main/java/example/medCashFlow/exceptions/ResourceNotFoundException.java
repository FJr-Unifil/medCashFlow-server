package example.medCashFlow.exceptions;

public abstract class ResourceNotFoundException extends MedCashFlowException {

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s não encontrado(a) com identificador: %s", resourceName, identifier));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
