package com.roundrobin_assignment.ticketpipeline.config.properties;

import com.roundrobin_assignment.ticketpipeline.flow.FlowId;

import java.util.Map;

public class FlowsProperties {
    private Map<FlowId, FlowProperties> map;

    public Map<FlowId, FlowProperties> getFlowsProperties() {
        return map;
    }

    public FlowsProperties setFlowsProperties(Map<FlowId, FlowProperties> map) {
        this.map = map;
        return this;
    }

    public static class FlowProperties {
        private int threadCount;

        public int getThreadCount() {
            return threadCount;
        }

        public FlowProperties setThreadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }
    }
}
