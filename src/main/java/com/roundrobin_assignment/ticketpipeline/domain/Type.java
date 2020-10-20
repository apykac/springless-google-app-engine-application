package com.roundrobin_assignment.ticketpipeline.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Type {
    @JsonProperty("problem") PROBLEM,
    @JsonProperty("incident") INCIDENT,
    @JsonProperty("question") QUESTION,
    @JsonProperty("task") TASK;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

}