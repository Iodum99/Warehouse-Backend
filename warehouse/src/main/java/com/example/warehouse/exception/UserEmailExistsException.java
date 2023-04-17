package com.example.warehouse.exception;

import java.io.Serial;

public class UserEmailExistsException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public UserEmailExistsException(String message) {
        super(message);
    }

}
