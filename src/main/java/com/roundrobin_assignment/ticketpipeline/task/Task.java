package com.roundrobin_assignment.ticketpipeline.task;

import com.roundrobin_assignment.ticketpipeline.flow.FlowId;

public interface Task extends Runnable {
    void destroy();

    FlowId getFlowId();
}
