package com.example.warehouse.exception;

import java.io.Serial;

public class UserInvalidPasswordException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserInvalidPasswordException(String message) {
        super(message);
    }
}
