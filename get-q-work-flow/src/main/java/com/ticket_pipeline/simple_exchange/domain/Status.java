package com.ticket_pipeline.simple_exchange.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("new") NEW,
    @JsonProperty("open") OPEN,
    @JsonProperty("pending") PENDING,
    @JsonProperty("hold") HOLD,
    @JsonProperty("solved") SOLVED,
    @JsonProperty("closed") CLOSED,
    @JsonProperty("deleted") DELETED
}