package example.medCashFlow.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class MultipleInvalidDataException extends RuntimeException {

    private final List<ApiValidationError> errors;

    public MultipleInvalidDataException(List<ApiValidationError> errors) {
        this.errors = errors;
    }

}