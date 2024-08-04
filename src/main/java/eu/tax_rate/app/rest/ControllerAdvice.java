package eu.tax_rate.app.rest;


import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

import static java.lang.String.format;

/**
 * Rest controller advice.
 */
@Log4j2
@RestControllerAdvice
public class ControllerAdvice {

    /**
     * Advice for common exceptions resulting in a bad request
     *
     * @param exception exception
     * @return BAD_REQUEST formatted nicely
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            {
                    IllegalArgumentException.class,
                    MissingServletRequestParameterException.class,
                    MethodArgumentTypeMismatchException.class
            }
    )
    public ErrorResponse handleValidationErrors(final Exception exception) {
        final HttpStatus badRequestResponseCode = HttpStatus.BAD_REQUEST;
        return ErrorResponse.builder()
                .status(badRequestResponseCode)
                .errorCode(String.valueOf(badRequestResponseCode.value()))
                .detail(badRequestResponseCode.getReasonPhrase())
                .message(prepareResponseMessage(exception, Level.INFO, badRequestResponseCode))
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RuntimeException.class, IllegalStateException.class})
    public ErrorResponse handleRuntimeException(final Exception exception) {
        final HttpStatus internalServerErrorCode = HttpStatus.INTERNAL_SERVER_ERROR;
        return ErrorResponse.builder()
                .status(internalServerErrorCode)
                .errorCode(String.valueOf(internalServerErrorCode.value()))
                .detail(internalServerErrorCode.getReasonPhrase())
                .message(prepareResponseMessage(exception, Level.ERROR, internalServerErrorCode))
                .build();
    }

    /**
     * Prepare error message to be sent back in api response.
     * The error message contains a unique error reference id for looking up the detail from the logs.
     *
     * @param exception exception that occurred.
     * @param logLevel  log level to log this exception with.
     * @return error message with an error reference id for lookup.
     */
    private String prepareResponseMessage(
            @NonNull final Exception exception,
            @NonNull final Level logLevel,
            @NonNull final HttpStatus httpResponseCode
    ) {
        final UUID errorId = UUID.randomUUID();
        log.log(
                logLevel,
                format(
                        "Exception of type '%s' occurred at rest controller with error reference id '%s' resulting in http response code '%s'",
                        exception.getClass().getName(),
                        errorId,
                        httpResponseCode
                ),
                exception
        );
        return format("Exception occurred with error reference id '%s'", errorId);
    }
}
