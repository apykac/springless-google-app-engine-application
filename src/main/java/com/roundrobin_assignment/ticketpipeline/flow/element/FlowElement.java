package com.roundrobin_assignment.ticketpipeline.flow.element;

public interface FlowElement<I, O> {
    O doFlowStep(I income);

    FlowElementId getFlowElementId();
}
