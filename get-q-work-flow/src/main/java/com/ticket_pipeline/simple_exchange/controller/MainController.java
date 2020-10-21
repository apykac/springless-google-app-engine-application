package com.ticket_pipeline.simple_exchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_context.Init;
import com.ticket_pipeline.simple_exchange.domain.dto.BaseResponse;
import com.ticket_pipeline.simple_exchange.net.server.AbstractController;
import com.ticket_pipeline.simple_utils.StringUtils;
import com.ticket_pipeline.simple_utils.log.LogLevel;
import com.ticket_pipeline.simple_utils.log.LoggerFactory;

@Component
public class MainController extends AbstractController {
    private final ResourceController resourceController;

    @Constructor
    public MainController(ObjectMapper objectMapper, ResourceController resourceController) {
        super(objectMapper);
        this.resourceController = resourceController;
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
                threadCount = resourceController.resizeThreadCount(threadCount);
                return new BaseResponse<String>().setPayload("Successful set thread count to " + threadCount);
            } catch (Exception e) {
                return new BaseResponse<String>().setPayload("Can't set new thread count: " + e.getMessage());
            }
        });
    }

    void getThreadCount() {
        getEntryPoint("/get/threadCount", (request, params) -> new BaseResponse<String>().setPayload("Thread count: " + resourceController.getThreadCount()));
    }

    void setDBPoolSize() {
        getEntryPoint("/set/dbPoolSize", (request, params) -> {
            try {
                int poolSize = Integer.parseInt(params.get("count"));
                poolSize = resourceController.resizeDBPoolSize(poolSize);
                return new BaseResponse<String>().setPayload("Successful set DB pool size to " + poolSize);
            } catch (Exception e) {
                return new BaseResponse<String>().setPayload("Can't set new DB pool size: " + StringUtils.exceptionToString(e));
            }
        });
    }

    void getDBPoolSize() {
        getEntryPoint("/get/dbPoolSize", (request, params) -> new BaseResponse<String>().setPayload("DB pool size: " + resourceController.getDBPoolSize()));
    }

    @Init(1)
    public void init() {
        setLogLevel();
        getLogLevel();
        setThreadCount();
        getThreadCount();
        setDBPoolSize();
        getDBPoolSize();
    }
}
