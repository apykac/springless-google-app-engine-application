package com.roundrobin_assignment.ticketpipeline.task;

import com.roundrobin_assignment.ticketpipeline.config.context.Environment;
import com.roundrobin_assignment.ticketpipeline.flow.FlowId;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkedFlowManager {
    private final Map<FlowId, FlowThreadContainer> workedFlows = new EnumMap<>(FlowId.class);

    public WorkedFlowManager() {
        Arrays.asList(FlowId.values()).forEach(flowId ->
                workedFlows.put(flowId, new FlowThreadContainer(Environment.getProp(flowId.name() + ".thread-count", 1, Integer.class)))
        );
    }

    public WorkedFlowManager addFlow(FlowId flowId) {
        FlowThreadContainer container = workedFlows.get(flowId);
        if (container != null) {
            container.workedFlows.getAndIncrement();
        }
        return this;
    }

    public WorkedFlowManager removeFlow(FlowId flowId) {
        FlowThreadContainer container = workedFlows.get(flowId);
        if (container != null) {
            container.workedFlows.getAndDecrement();
        }
        return this;
    }

    public boolean isMax(FlowId flowId) {
        FlowThreadContainer container = workedFlows.get(flowId);
        if (container != null) {
            return container.isMax();
        } else {
            return true;
        }
    }

    private static class FlowThreadContainer {
        private final AtomicInteger workedFlows = new AtomicInteger(0);
        private final int max;

        public FlowThreadContainer(int max) {
            this.max = max;
        }

        public boolean isMax() {
            return max <= workedFlows.get();
        }
    }
}
