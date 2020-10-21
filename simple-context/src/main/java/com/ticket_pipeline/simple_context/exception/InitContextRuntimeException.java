package com.ticket_pipeline.simple_context.exception;

public class InitContextRuntimeException extends RuntimeException {
    public InitContextRuntimeException() {
    }

    public InitContextRuntimeException(String message) {
        super(message);
    }

    public InitContextRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitContextRuntimeException(Throwable cause) {
        super(cause);
    }

    public InitContextRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
