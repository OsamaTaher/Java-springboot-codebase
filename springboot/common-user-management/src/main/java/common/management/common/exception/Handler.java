package common.management.common.exception;

import common.management.common.payload.response.BasicResponse;
import common.management.common.payload.response.ValidationError;
import common.management.common.payload.response.ValidationErrorResponse;
import common.management.common.util.OperationStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

import static common.management.common.util.OperationStatus.CUSTOM_HTTP_STATUS_VALIDATION_ERROR;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class Handler {
    private final OperationStatus operationStatus;

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception) {
        var errorList = exception.getFieldErrors().stream().map(err -> new ValidationError(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
        return ResponseEntity.status(CUSTOM_HTTP_STATUS_VALIDATION_ERROR).body(new ValidationErrorResponse(exception.getErrorCount(), errorList));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BasicResponse> handleHttpMessageNotReadable(Exception exception) {
        log.error("[EXCEPTION] HttpMessageNotReadable exception:  {} {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Message parsing failed"
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BasicResponse> handleIllegalArgumentException(Exception exception) {
        log.error("[EXCEPTION] IllegalArgumentException exception:  {} {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicResponse> handleException(Exception exception) {
        log.error("[EXCEPTION] un-caught exception: {},{}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BasicResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected Error"
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BasicResponse> handleAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BasicResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access denied"
        ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BasicResponse> handleAccessDeniedException(HandlerMethodValidationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage()
        ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BasicResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage()
        ));
    }


}
