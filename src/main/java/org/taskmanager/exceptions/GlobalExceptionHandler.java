package org.taskmanager.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.taskmanager.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice(annotations = RestController.class)
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
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e, HttpServletRequest request){
        ErrorResponse response = new ErrorResponse("Internal Server Error: " + e.getMessage(), LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse response = new ErrorResponse(
                "Błąd walidacji: " + errorMessage,
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

     @ExceptionHandler(MaxUploadSizeExceededException.class)
     public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, HttpServletRequest request) {
         ErrorResponse response = new ErrorResponse(
                 "Plik jest za duży. Maksymalny rozmiar to 5MB",
                 LocalDateTime.now(),
                 HttpStatus.PAYLOAD_TOO_LARGE.value(),
                 request.getRequestURI()
         );
         return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
     }
}
