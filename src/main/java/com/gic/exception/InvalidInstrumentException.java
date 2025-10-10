package com.gic.exception;

public class InvalidInstrumentException extends BusinessException {
    public InvalidInstrumentException(String message) {
        super("INVALID_INSTRUMENT", message);
    }
}
