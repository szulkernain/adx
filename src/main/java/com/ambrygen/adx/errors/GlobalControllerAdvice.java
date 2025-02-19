package com.ambrygen.adx.errors;

import com.ambrygen.adx.dto.ADXResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
class GlobalControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        int count = ex.getBindingResult().getErrorCount();
        StringBuilder builder = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            builder.append(fieldError.getField() + ": ");
            builder.append(fieldError.getDefaultMessage());
            if (count > 1) {
                builder.append(", ");
            }
        });
        String message = builder.toString();
        if (message.endsWith(", ")) {
            message = message.substring(0, message.length() - 2);
        }
        return ResponseEntity.status(status).body(new ADXResponseDTO(true,message,""));
    }

    // fallback method
    @ExceptionHandler(Exception.class) // exception handled
    public ResponseEntity<ADXResponseDTO> handleExceptions(
            Exception e
    ) {
        log.error("Unexpected error", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500

        return ResponseEntity.status(status).body(new ADXResponseDTO(true,"Server error - please try again or contact customer support at adx-support@ambrygenetics.com",""));
    }



    @ExceptionHandler({InvalidTokenException.class, ExpiredJwtException.class, AccessDeniedException.class}) // exception handled
    public ResponseEntity<ADXResponseDTO> handleInvalidTokenException(Exception e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401

        return ResponseEntity.status(status).body(new ADXResponseDTO(true,e.getMessage(),""));
    }

    @ExceptionHandler({ResourceNotFoundException.class,
            InvalidVINException.class,
            BadCredentialsException.class,
            UserAccountNotVerifiedException.class,
            InvalidFileTypeException.class})
    public ResponseEntity<ADXResponseDTO> handleBadUserInput(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        log.error("Problem", e);
        ADXResponseDTO responseDTO = new ADXResponseDTO(true,e.getMessage(),null);
        return new ResponseEntity<ADXResponseDTO>(responseDTO,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public ResponseEntity<ADXResponseDTO> handleUserAlreadyExists(Exception e) {
        HttpStatus status = HttpStatus.CONFLICT; // 409
        return ResponseEntity.status(status).body(new ADXResponseDTO(true,e.getMessage(),null));
    }
}
