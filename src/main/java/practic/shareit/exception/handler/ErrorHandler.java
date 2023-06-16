package practic.shareit.exception.handler;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import practic.shareit.exception.BadRequestException;
import practic.shareit.exception.NotAvailableException;
import practic.shareit.exception.NotFoundException;
import practic.shareit.exception.OperationAccessException;
import practic.shareit.exception.TimeDataException;

import javax.validation.ValidationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAvailableException(NotAvailableException e) {
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(final ConstraintViolationException e) {
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUnknownDataException(NotFoundException e) {
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerAccessException(final OperationAccessException e) {
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Exception e) {
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownDataException(MethodArgumentNotValidException e) {
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownDataException(TimeDataException e) {
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable throwable) {
        return ErrorResponse.builder().error(throwable.getMessage()).build();
    }

    @Data
    @Builder
    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}