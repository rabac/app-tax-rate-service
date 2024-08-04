package eu.tax_rate.app.rest;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Api error response object used in the {@link ControllerAdvice}
 */
@Data
@Builder(toBuilder = true)
public class ErrorResponse {
    /** Http status */
    private HttpStatus status;

    /** Error code */
    private String errorCode;

    /** Error message with description of the error */
    private String message;

    /** Description of the http status code */
    private String detail;
}
