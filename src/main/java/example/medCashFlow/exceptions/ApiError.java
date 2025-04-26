package example.medCashFlow.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@JsonTypeInfo(
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        visible = true
)
@JsonTypeIdResolver(LowerCaseClassNameResolver.class)
public class ApiError {

    private int status;
    private String error;
    private String message;
    private String path;

    @Builder.Default
    private List<ApiSubError> subErrors = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

}
