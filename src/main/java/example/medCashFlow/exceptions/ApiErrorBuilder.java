package example.medCashFlow.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ApiErrorBuilder {

    private HttpStatus status;
    private String message;
    private String path;
    private List<ApiValidationError> subErrors = new ArrayList<>();
    private final Instant timestamp = Instant.now();

    private ApiErrorBuilder() {
        this.path = resolveRequestPath();
    }

    public static ApiErrorBuilder builder() {
        return new ApiErrorBuilder();
    }

    public ApiErrorBuilder status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ApiErrorBuilder message(String message) {
        this.message = message;
        return this;
    }

    public ApiErrorBuilder path(String path) {
        this.path = path;
        return this;
    }

    public ApiErrorBuilder subErrors(List<ApiValidationError> subErrors) {
        if (subErrors != null) {
            this.subErrors = subErrors;
        }
        return this;
    }

    public ApiError build() {
        return new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                subErrors,
                timestamp
        );
    }

    private String resolveRequestPath() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getRequestURI();
        }
        return "Unknown Path";
    }
}

