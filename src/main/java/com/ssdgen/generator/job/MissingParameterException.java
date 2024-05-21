package com.ssdgen.generator.job;

public class MissingParameterException extends Exception {

    public MissingParameterException(String message) {
        super(message);
    }

    public MissingParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
