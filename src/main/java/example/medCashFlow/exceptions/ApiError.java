package example.medCashFlow.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        String path,
        List<ApiValidationError> subErrors,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant timestamp
) {}