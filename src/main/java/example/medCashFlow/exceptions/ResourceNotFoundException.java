package example.medCashFlow.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
public abstract class ResourceNotFoundException extends MedCashFlowException {

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s não encontrado(a) com id: %s", resourceName, identifier));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
