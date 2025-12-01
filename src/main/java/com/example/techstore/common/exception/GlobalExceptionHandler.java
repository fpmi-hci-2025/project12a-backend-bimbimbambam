package com.example.techstore.common.exception;

import com.example.techstore.cart.util.CartException;
import com.example.techstore.catalog.util.CatalogException;
import com.example.techstore.order.util.OrderException;
import com.example.techstore.review.utils.ReviewException;
import com.example.techstore.user.util.AuthException;
import com.example.techstore.user.util.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal server error: " + ex.getMessage()));
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UserException e) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()));
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                        e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ReviewException e) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()
                ));
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorResponse> handleException(OrderException e) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()
                ));
    }

    @ExceptionHandler(CatalogException.class)
    public ResponseEntity<ErrorResponse> handleException(CatalogException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage()
                ));
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<ErrorResponse> handleException(CartException e) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()
                ));
    }
}