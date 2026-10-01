package com.demon_invasion.backend.exception;

import com.demon_invasion.backend.model.dto.DtoApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<DtoApiError> buildResponse(HttpStatus status, String errorCode, String message, HttpServletRequest request) {
        DtoApiError error = new DtoApiError(
                status,
                errorCode,
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AlreadyExist.class)
    public ResponseEntity<DtoApiError> handleAlreadyExist(AlreadyExist ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "RESOURCE_ALREADY_EXISTS", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<DtoApiError> handleInvalidCreds(InvalidCredentialsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "AUTH_FAILED", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<DtoApiError> handleInvalidCode(InvalidVerificationCodeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_CODE", ex.getMessage(), request);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<DtoApiError> handleTooManyRequests(TooManyRequestsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DtoApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" "));
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DtoApiError> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Le corps de la requête est invalide ou absent.", request);
    }

    // Capture toutes les autres erreurs non prévues
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DtoApiError> handleGlobal(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Une erreur inattendue est survenue", request);
    }
}
