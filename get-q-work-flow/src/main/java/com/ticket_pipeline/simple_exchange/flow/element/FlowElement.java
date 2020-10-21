package com.ticket_pipeline.simple_exchange.flow.element;

public interface FlowElement<I, O> {
    O doFlowStep(I income);

    FlowElementId getFlowElementId();
}
