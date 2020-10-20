package com.roundrobin_assignment.ticketpipeline.net;

public class ResponseEntity<T> {
    private final T body;
    private final int code;

    ResponseEntity(T body, int code) {
        this.body = body;
        this.code = code;
    }

    public T getBody() {
        return body;
    }

    public int getCode() {
        return code;
    }
}
