package com.rw.carriages.controllers;

import by.iba.railway.eticket.xml.exception.BusinessSystemException;
import by.iba.railway.eticket.xml.exception.XmlParserSystemException;
import com.rw.carriages.dto.ErrorMessage;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request", response = ErrorMessage.class, responseContainer = "List"),
        @ApiResponse(code = 503, message = "Service Unavailable"),
        @ApiResponse(code = 504, message = "Gateway Timeout")
})
public class BaseController {
    public enum ERROR_PREFIX {validation, system, express}
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleInvalidRequest(ConstraintViolationException e, WebRequest request) {
        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();
        ErrorMessage errorMessage = new ErrorMessage(ERROR_PREFIX.validation+".error",e.getLocalizedMessage());
        errors.add(errorMessage);
        return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidRequest(MethodArgumentNotValidException e, WebRequest request) {
        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();
        if(e.getBindingResult().hasErrors()) {
            for(ObjectError oe: e.getBindingResult().getAllErrors()) {
                ErrorMessage errorMessage = new ErrorMessage(oe.getCodes()[0],oe.getDefaultMessage());
                errors.add(errorMessage);
            }
        }
        return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessSystemException.class)
    public ResponseEntity<?> handleInvalidRequest(BusinessSystemException e, WebRequest request) {
        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();
        ErrorMessage errorMessage = new ErrorMessage(ERROR_PREFIX.express+"."+e.getCode(),e.getMessage());
        errors.add(errorMessage);
        return new ResponseEntity(errors, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(XmlParserSystemException.class)
    public ResponseEntity<?> handleInvalidRequest(XmlParserSystemException e, WebRequest request) {
        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();
        ErrorMessage errorMessage;
        HttpStatus status;
        if("timeout".equals(e.getMessage())) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            return new ResponseEntity(status);
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            return new ResponseEntity(errors, status);
        }
    }
    @ExceptionHandler(ConnectException.class)
    protected List<ErrorMessage> handleConnectException(ConnectException e) {
        List<ErrorMessage> errors = new ArrayList<>();
        errors.add(new ErrorMessage(ERROR_PREFIX.system+".database_error", e.getMessage()));
        return errors;
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected List<ErrorMessage> handleDataAccessException(EmptyResultDataAccessException e) {
        List<ErrorMessage> errors = new ArrayList<>();
        errors.add(new ErrorMessage(ERROR_PREFIX.system+".database_error", e.getMessage()));
        return errors;
    }

    @ExceptionHandler(SQLException.class)
    protected List<ErrorMessage> handleSQLException(SQLException e) {
        List<ErrorMessage> errors = new ArrayList<>();
        errors.add(new ErrorMessage(ERROR_PREFIX.system+".database_error", e.getMessage()));
        return errors;
    }

}