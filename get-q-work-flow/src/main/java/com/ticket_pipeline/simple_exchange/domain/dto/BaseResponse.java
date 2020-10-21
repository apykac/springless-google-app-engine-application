package com.ticket_pipeline.simple_exchange.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseResponse<T> {
    private T payload;
    private Throwable t;

    public T getPayload() {
        return this.payload;
    }

    public Throwable getT() {
        return this.t;
    }

    public BaseResponse<T> setPayload(T payload) {
        this.payload = payload;
        return this;
    }

    public BaseResponse<T> setException(Throwable t) {
        this.t = t;
        return this;
    }
}