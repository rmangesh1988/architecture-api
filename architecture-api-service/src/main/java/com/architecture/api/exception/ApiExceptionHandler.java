package com.architecture.api.exception;

import com.architecture.api.dto.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.ServletException;
import java.util.Collections;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IncorrectHeightPlateausException.class)
    public ResponseEntity<ApiException> handleEntityNotFoundException(IncorrectHeightPlateausException e) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ApiException.builder()
                        .httpStatus(BAD_REQUEST)
                        .message(e.getLocalizedMessage())
                        .errors(Collections.EMPTY_LIST)
                        .build());
    }

    @ExceptionHandler(ConcurrentDataModificationException.class)
    public ResponseEntity<ApiException> handleConcurrentDataModificationException(ConcurrentDataModificationException e) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ApiException.builder()
                        .httpStatus(BAD_REQUEST)
                        .message(e.getLocalizedMessage())
                        .errors(Collections.EMPTY_LIST)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleGenericException(Exception ex) throws Exception {
        if (ex instanceof ServletException) {
            throw ex;
        }
        log.error("Internal server error: " + ex.getMessage(), ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ApiException.builder()
                        .httpStatus(INTERNAL_SERVER_ERROR)
                        .message(ex.getLocalizedMessage())
                        .errors(Collections.EMPTY_LIST).build());
    }

}
