package com.cfs.RideTap.ControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import com.cfs.RideTap.dtos.errorDTO.ErrorResponse;
import com.cfs.RideTap.exceptions.InsufficientBalanceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentException(MethodArgumentNotValidException ex) {
    Map<String, String> errorMap = new HashMap<>();

    BindingResult bindingResult = ex.getBindingResult();

    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    ErrorResponse errorResponse = new ErrorResponse(errorMap, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("error", "Response status: " + ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(errorMap, ex.getStatusCode());

    return new ResponseEntity<>(errorResponse, ex.getStatusCode());
  }

  @ExceptionHandler(InsufficientBalanceException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex) {
    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("error", "" + ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(errorMap, HttpStatus.BAD_REQUEST);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DateTimeParseException.class)
  public ResponseEntity<ErrorResponse> handleDateTimeParsingException(DateTimeParseException ex) {
    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("error", "Invalid Date Time format. Must be (yyyy-MM-dd'T'HH:mm:ss)");

    ErrorResponse errorResponse = new ErrorResponse(errorMap, HttpStatus.BAD_REQUEST);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("error", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
