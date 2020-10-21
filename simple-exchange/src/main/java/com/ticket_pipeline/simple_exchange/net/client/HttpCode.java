package com.ticket_pipeline.simple_exchange.net.client;

public enum HttpCode {
    OK, ERROR;

    public static HttpCode getHttpCode(int code) {
        return code / 100 == 2 ? OK : ERROR;
    }
}
