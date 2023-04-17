package com.example.warehouse.exception;

import java.io.Serial;

public class VerificationTokenNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public VerificationTokenNotFoundException(String message) {
        super(message);
    }
}
