package com.example.warehouse.exception;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Date;

@ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage userNotFoundException(UserNotFoundException ex){
        ex.printStackTrace();
        return new ErrorMessage(404, new Date(), "User Not Found!",
                ex.getLocalizedMessage());
    }

    @ExceptionHandler(UserUsernameExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage userUsernameExists(UserUsernameExistsException ex){
        ex.printStackTrace();
        return new ErrorMessage(409, new Date(), "Username already in use!",
                ex.getLocalizedMessage());
    }

    @ExceptionHandler(UserEmailExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage userEmailExists(UserEmailExistsException ex){
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
    public ErrorMessage internalServerError(Exception ex){
        ex.printStackTrace();
        return new ErrorMessage(500, new Date(), ex.getMessage(),
                "Oops! There seems to be an error... Please try again later.");
    }


    // Database Exceptions Handling
    @ExceptionHandler(value = {
            JDBCConnectionException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage databaseConnectionException(Exception ex){
        ex.printStackTrace();
        return new ErrorMessage(500, new Date(), "Internal Server Error",
                "Oops! There seems to be an error communicating with the database.");
    }

    // Token Exceptions Handling
    @ExceptionHandler(VerificationTokenNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage verificationTokenNotFoundException(VerificationTokenNotFoundException ex){
        ex.printStackTrace();
        return new ErrorMessage(404, new Date(), "Token already used or expired",
                ex.getLocalizedMessage());
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    @ResponseStatus(value = HttpStatus.GONE)
    public ErrorMessage verificationTokenExpiredException(VerificationTokenExpiredException ex){
        ex.printStackTrace();
        return new ErrorMessage(498, new Date(), "Token expired",
                ex.getLocalizedMessage());
    }

    // Asset Exceptions Handling
    @ExceptionHandler(AssetNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage AssetNotFoundException(AssetNotFoundException ex){
        ex.printStackTrace();
        return new ErrorMessage(404, new Date(), "Asset not found",
                ex.getLocalizedMessage());
    }

    @ExceptionHandler(AssetUnsupportedExtensionException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage AssetUnsupportedExtensionException(AssetUnsupportedExtensionException ex){
        ex.printStackTrace();
        String type = ex.getMessage();
        String supportedTypes = switch (type) {
            case "OBJECT" -> ".3ds .fbx .obj .dae";
            case "TEXTURE" -> ".bmp .png .jpg";
            case "AUDIO" -> ".mp3 .flac .wav";
            default -> ".fbx .3ds";
        };
        return new ErrorMessage(400, new Date(),
                "Asset does not have supported extension type for " + type + '\n' +
                "Supported types: " + supportedTypes,
                ex.getLocalizedMessage());
    }
}
