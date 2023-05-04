package com.example.warehouse.exception;

import java.io.Serial;

public class AssetUnsupportedExtensionException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public AssetUnsupportedExtensionException(String message) {
        super(message);
    }
}
