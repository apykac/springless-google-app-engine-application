package com.ticket_pipeline.simple_exchange.domain;

public enum TaskStatus {
    OK(1),
    ERROR(0);

    private final int value;

    TaskStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
