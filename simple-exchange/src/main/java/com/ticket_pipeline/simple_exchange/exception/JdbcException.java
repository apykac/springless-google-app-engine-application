package com.ticket_pipeline.simple_exchange.exception;

public class JdbcException extends Exception {
    private static final long serialVersionUID = 8271389710402028818L;

    public JdbcException(Throwable cause) {
        super(cause);
    }
}
