package example.medCashFlow.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String entity;

    private final String propertyName;

    private final String value;

    public ResourceNotFoundException(String entity, String propertyName, String value) {
        this.entity = entity;
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public String getMessage() {
        return String.format("%s not found with %s: %s", entity, propertyName, value);
    }
}
