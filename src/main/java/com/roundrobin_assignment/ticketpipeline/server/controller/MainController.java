package com.roundrobin_assignment.ticketpipeline.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.config.context.Init;
import com.roundrobin_assignment.ticketpipeline.domain.dto.BaseResponse;
import com.roundrobin_assignment.ticketpipeline.task.FlowThreadManager;
import com.roundrobin_assignment.ticketpipeline.util.log.LogLevel;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

@Component
public class MainController extends AbstractController {
    private final FlowThreadManager flowThreadManager;

    @Constructor
    public MainController(ObjectMapper objectMapper, FlowThreadManager flowThreadManager) {
        super(objectMapper);
        this.flowThreadManager = flowThreadManager;
    }

    void setLogLevel() {
        getEntryPoint("/set/logLevel", (request, params) -> {
            String level = params.get("level");
            LogLevel logLevel = LogLevel.getLogLevel(level);
            if (logLevel != null) {
                LoggerFactory.setLogLevel(logLevel);
                return new BaseResponse<String>().setPayload("Successful set log level to " + level);
            } else {
                return new BaseResponse<String>().setPayload("Can't set log level to " + level);
            }
        });
    }

    void getLogLevel() {
        getEntryPoint("/get/logLevel", (request, params) -> new BaseResponse<String>().setPayload("LogLevel: " + LoggerFactory.getLogLevel().getName()));
    }

    void setThreadCount() {
        getEntryPoint("/set/threadCount", (request, params) -> {
            try {
                int threadCount = Integer.parseInt(params.get("count"));
                threadCount = flowThreadManager.setThreadCount(threadCount);
                return new BaseResponse<String>().setPayload("Successful set thread count to " + threadCount);
            } catch (Exception e) {
                return new BaseResponse<String>().setPayload("Can't set new thread count " + e.getMessage());
            }
        });
    }

    void getThreadCount() {
        getEntryPoint("/get/threadCount", (request, params) -> new BaseResponse<String>().setPayload("Thread count: " + flowThreadManager.getThreadCount()));
    }

    @Init(1)
    public void init() {
        setLogLevel();
        getLogLevel();
        setThreadCount();
        getThreadCount();
    }
}
