package com.onlineshop.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Insufficient Stock");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(ProductUnavailableException.class)
    public ProblemDetail handleProductUnavailable(ProductUnavailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Product Unavailable");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation Failed");
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InvalidGtinException.class)
    public ProblemDetail handleInvalidGtin(InvalidGtinException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid GTIN");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ProblemDetail handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Invalid Status Transition");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support if the problem persists.");
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
