package com.roundrobin_assignment.ticketpipeline.domain.dto;

public class SetLogLevelRequest {
    private String logLevel;

    public String getLogLevel() {
        return logLevel;
    }

    public SetLogLevelRequest setLogLevel(String logLevel) {
        this.logLevel = logLevel;
        return this;
    }
}
