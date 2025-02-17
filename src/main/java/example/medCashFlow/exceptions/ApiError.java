package example.medCashFlow.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {

    private int status;
    private String title;
    private String description;
    private String technicalDetails;
    private LocalDateTime timestamp;

}
