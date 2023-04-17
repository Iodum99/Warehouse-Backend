package com.example.warehouse.exception;

import java.io.Serial;

public class VerificationTokenExpiredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public VerificationTokenExpiredException(String message) {
        super(message);
    }
}
