package com.roundrobin_assignment.ticketpipeline.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roundrobin_assignment.ticketpipeline.domain.dto.BaseResponse;
import com.roundrobin_assignment.ticketpipeline.domain.dto.SetLogLevelRequest;
import com.roundrobin_assignment.ticketpipeline.task.GetQueueTask;
import com.roundrobin_assignment.ticketpipeline.util.log.LogLevel;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

public class MainController extends AbstractController {
    private final GetQueueTask getQueueTask;

    public MainController(ObjectMapper objectMapper, GetQueueTask getQueueTask) {
        super(objectMapper);

        this.getQueueTask = getQueueTask;

        runGetQueueTask();
        setLogLevel();
    }

    void runGetQueueTask() {
        getEntryPoint("/runGetQueueTask", (request, params) -> {
            getQueueTask.run();
            return "Got it [runGetQueueTask]";
        });
    }

    void setLogLevel() {
        postEntryPoint("/setLogLevel", (request, params) -> {
            LogLevel logLevel = LogLevel.getLogLevel(request.getLogLevel());
            if (logLevel != null) {
                LoggerFactory.setLogLevel(logLevel);
                return new BaseResponse<String>().setPayload("Successful set log level to " + logLevel);
            } else {
                return new BaseResponse<String>().setPayload("Can't set log level to " + request.getLogLevel());
            }
        }, SetLogLevelRequest.class);
    }
}
