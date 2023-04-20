package com.example.warehouse.exception;

import java.io.Serial;

public class AssetNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public AssetNotFoundException(String message) {
        super(message);
    }
}
