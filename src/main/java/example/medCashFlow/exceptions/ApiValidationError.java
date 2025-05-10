package example.medCashFlow.exceptions;

public record ApiValidationError(String object, String field, Object rejectedValue) {
    public ApiValidationError {
        object = object.toLowerCase();
    }
}