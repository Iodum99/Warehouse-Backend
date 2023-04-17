package com.example.warehouse.exception;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage userNotFoundException(UserNotFoundException ex, WebRequest request){
        ex.printStackTrace();
        return new ErrorMessage(404, new Date(), "User Not Found!",
                ex.getLocalizedMessage());
    }

    @ExceptionHandler(UserUsernameExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage userUsernameExists(UserUsernameExistsException ex, WebRequest request){
        ex.printStackTrace();
        return new ErrorMessage(409, new Date(), "Username already in use!",
                ex.getLocalizedMessage());
    }

    @ExceptionHandler(UserEmailExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage userEmailExists(UserEmailExistsException ex, WebRequest request){
        ex.printStackTrace();
        return new ErrorMessage(409, new Date(), "E-mail already in use!",
                ex.getLocalizedMessage());
    }

    // General Exceptions Handling
    @ExceptionHandler(value = {
            HttpServerErrorException.InternalServerError.class,
            NullPointerException.class,
            IllegalStateException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage internalServerError(Exception ex, WebRequest request){
        ex.printStackTrace();
        return new ErrorMessage(500, new Date(), ex.getMessage(),
                "Oops! There seems to be an error... Please try again later.");
    }


    // Database Exceptions Handling
    @ExceptionHandler(value = {
            JDBCConnectionException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage databaseConnectionException(Exception ex, WebRequest request){
        ex.printStackTrace();
        return new ErrorMessage(500, new Date(), "Internal Server Error",
                "Oops! There seems to be an error communicating with the database.");
    }
}
