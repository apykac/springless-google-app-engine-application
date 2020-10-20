package com.roundrobin_assignment.ticketpipeline.task;

import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.config.context.Destroy;
import com.roundrobin_assignment.ticketpipeline.config.context.Environment;
import com.roundrobin_assignment.ticketpipeline.flow.FlowId;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTaskManager {
    private final Map<FlowId, ScheduledExecutorService> scheduledExecutorServiceMap = new EnumMap<>(FlowId.class);

    @Constructor
    public ScheduledTaskManager(List<Task> tasks) {
        tasks.forEach(task -> {
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(task, 1, Environment.getProp(task.getFlowId().name() + ".fixed-delay", 500, Integer.class), TimeUnit.MILLISECONDS);
            scheduledExecutorServiceMap.put(
                    task.getFlowId(),
                    new ScheduledThreadPoolExecutor(Environment.getProp(task.getFlowId().name() + ".fixed-delay", 500, Integer.class)));
        });
    }

    @Destroy
    public void destroy() {
        scheduledExecutorServiceMap.values().forEach(ExecutorService::shutdown);
    }
}
