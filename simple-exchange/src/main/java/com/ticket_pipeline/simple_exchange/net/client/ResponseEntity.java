package com.ticket_pipeline.simple_exchange.net.client;

import java.util.Objects;

public class ResponseEntity<T> {
    private final T body;
    private final HttpCode httpCode;
    private final int code;

    ResponseEntity(T body, int code, HttpCode httpCode) {
        this.body = body;
        this.code = code;
        this.httpCode = httpCode;
    }

    public T getBody() {
        return body;
    }

    public int getCode() {
        return code;
    }

    public boolean isOk() {
        return Objects.equals(httpCode, HttpCode.OK);
    }
}
