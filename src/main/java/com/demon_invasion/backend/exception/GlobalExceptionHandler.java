package com.demon_invasion.backend.exception;

import com.demon_invasion.backend.model.dto.DtoApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    // Capture toutes les autres erreurs non prévues
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DtoApiError> handleGlobal(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Une erreur inattendue est survenue", request);
    }
}
