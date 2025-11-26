package org.taskmanager.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.taskmanager.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ErrorResponse createErrorResponse(Exception e, HttpStatus status, HttpServletRequest request){
        return new ErrorResponse(e.getMessage(), LocalDateTime.now(), status.value(),request.getRequestURI());
    }
    @ExceptionHandler(InsufficientDataProvidedException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientDataProvided(InsufficientDataProvidedException e, HttpServletRequest request){
        ErrorResponse response = createErrorResponse(e,HttpStatus.BAD_REQUEST,request);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
        ErrorResponse response = createErrorResponse(e,HttpStatus.NOT_FOUND,request);
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }
}
