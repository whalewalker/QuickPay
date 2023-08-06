package com.quickpay.web.exception;

import com.quickpay.data.dto.ErrorDTO;
import com.quickpay.data.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class, AccountException.class, InsufficientBalanceException.class})
    public ResponseEntity<ResponseDTO> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(new ResponseDTO(false, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleGenericException(Exception ex) {
        log.info("ERROR OCCUR ==> {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDTO(false, "An error occurred"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDTO methodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult result = e.getBindingResult();
        List<FieldError> errors = result.getFieldErrors();
        return buildErrorDTO(errors);
    }


    private ErrorDTO buildErrorDTO(List<FieldError> errors) {
        ErrorDTO errorResponse = new ErrorDTO(HttpStatus.BAD_REQUEST.value(),  "Validation error. Check 'errors' field for details.");

        for (FieldError fieldError : errors){
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errorResponse;
    }


}

