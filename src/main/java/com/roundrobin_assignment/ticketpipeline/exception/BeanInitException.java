package com.roundrobin_assignment.ticketpipeline.exception;

public class BeanInitException extends RuntimeException {
    public BeanInitException() {
    }

    public BeanInitException(String message) {
        super(message);
    }

    public BeanInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanInitException(Throwable cause) {
        super(cause);
    }

    public BeanInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
