package com.gic.exception;

public class InsufficientLimitException extends BusinessException {
    public InsufficientLimitException(String message) {
        super("INSUFFICIENT_LIMIT", message);
    }
}
