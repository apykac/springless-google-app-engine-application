package com.ticket_pipeline.simple_exchange.controller;

import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_exchange.jdbc.datacource.DataSource;
import com.ticket_pipeline.simple_exchange.task.FlowThreadManager;

@Component
public class ResourceController {
    private final FlowThreadManager flowThreadManager;
    private final DataSource dataSource;

    @Constructor
    public ResourceController(FlowThreadManager flowThreadManager, DataSource dataSource) {
        this.flowThreadManager = flowThreadManager;
        this.dataSource = dataSource;
    }

    public int getThreadCount() {
        return flowThreadManager.getThreadCount();
    }

    public int resizeThreadCount(int newThreadCount) {
        return flowThreadManager.resizeThreadCount(newThreadCount);
    }

    public int getDBPoolSize() {
        return dataSource.getPoolSize();
    }

    public int resizeDBPoolSize(int newThreadPoolSize) {
        if (newThreadPoolSize > 0) {
            flowThreadManager.destroy();
            dataSource.resizeTreadPool(newThreadPoolSize);
            flowThreadManager.init();
        }
        return dataSource.getPoolSize();
    }
}
