package example.medCashFlow.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiValidationError extends ApiSubError {
    private String object;
    private String field;
    private Object rejectedValue;

    public ApiValidationError(String object, String field, Object rejectedValue) {
        this.object = object.toLowerCase();
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

}
