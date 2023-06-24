package org.scenter.onlineshop.web.exception;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.scenter.onlineshop.common.exception.ApiError;
import org.scenter.onlineshop.common.exception.ElementIsPresentedException;
import org.scenter.onlineshop.common.exception.IllegalFormatException;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class, UsernameNotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(Exception exception,
                                             HttpServletRequest request){
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NoSuchElementException.class, ElementIsPresentedException.class,
            FileUploadException.class, IOException.class, AuthenticationException.class})
    public ResponseEntity<ApiError> handleNoSuchElementException(Exception exception,
                                                                 HttpServletRequest request){
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IllegalFormatException.class)
    public ResponseEntity<ApiError> handleIllegalFormatException(IllegalFormatException exception,
                                                                 HttpServletRequest request){
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                exception.getMessage(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
    @ExceptionHandler(AccessException.class)
    public ResponseEntity<ApiError> handleAccessException(AccessException exception,
                                                          HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                exception.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }
}
