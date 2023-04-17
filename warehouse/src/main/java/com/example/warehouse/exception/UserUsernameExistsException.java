package com.example.warehouse.exception;

import java.io.Serial;

public class UserUsernameExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserUsernameExistsException(String message) {
        super(message);
    }
}
