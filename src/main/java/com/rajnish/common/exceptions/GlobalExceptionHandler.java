package com.rajnish.common.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException exception){
        ApiError apiError=new ApiError(HttpStatus.BAD_REQUEST,exception.getMessage());
        log.error(apiError.toString(),exception);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException exception){
        ApiError apiError=new ApiError(HttpStatus.NOT_FOUND,exception.getResourceName()+" with id "+exception.getResourceId()+" Not found ");
        log.error(apiError.toString(),exception);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInputValidationError(MethodArgumentNotValidException exception){

        List<ApiFieldError> apiFieldErrors=exception.getBindingResult().getFieldErrors().stream()
                .map(error->new ApiFieldError(error.getField(),error.getDefaultMessage()))
                .toList();


        ApiError apiError=new ApiError(HttpStatus.BAD_REQUEST,"Input Validation Failed", apiFieldErrors);
        log.error(apiError.toString(),exception);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }
}
