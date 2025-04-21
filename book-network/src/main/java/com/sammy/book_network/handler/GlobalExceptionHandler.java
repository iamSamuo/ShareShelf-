package com.sammy.book_network.handler;

import com.sammy.book_network.exception.OperationNotPermittedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.sammy.book_network.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //  account locked exception handling
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ExceptionResponse
                        .builder()
                        .businessErrorCode(ACCOUNT_lOCKED.getCode())
                        .businessErrorDescription(ACCOUNT_lOCKED.getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    // account disabled exception handling
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ExceptionResponse
                        .builder()
                        .businessErrorCode(ACCOUNT_DISABLED.getCode())
                        .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    // bad credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ExceptionResponse
                        .builder()
                        .businessErrorCode(BAD_CREDENTIALS.getCode())
                        .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                        .error(BAD_CREDENTIALS.getDescription())
                        .build());
    }

    // messaging exception when sending email
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse
                        .builder()
                        .error(exp.getMessage())
                        .build());
    }

    // handle invalid exception within the application which might happen in places where we have @Valid annotation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
        // create a set(does not allow duplicates) of errors
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors().forEach(error -> {
            var errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .validationErrors(errors)
                        .build());
    }

    // handle any other exception that is not yet handled within the application
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        // log the exception
        exp.printStackTrace(); // prints it to the console
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse
                        .builder().businessErrorDescription("Internal error, please contact the admin")
                        .error(exp.getMessage())
                        .build());
    }

    // operation not permitted exception
    // messaging exception when wrong user updates book status
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse
                        .builder()
                        .error(exp.getMessage())
                        .build());
    }

}
