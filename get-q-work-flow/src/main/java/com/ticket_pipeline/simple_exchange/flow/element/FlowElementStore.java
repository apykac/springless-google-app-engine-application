package com.ticket_pipeline.simple_exchange.flow.element;

import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ticket_pipeline.simple_utils.Assert.notNull;

@Component
public class FlowElementStore {
    private final Map<FlowElementId, FlowElement<?, ?>> flowElementMap;

    @Constructor
    public FlowElementStore(List<FlowElement<?, ?>> flowElements) {
        this.flowElementMap = flowElements.stream()
                .collect(() -> new HashMap<>((int) Math.min(flowElements.size() / 0.75, 16), 1),
                        (m, fe) -> m.put(fe.getFlowElementId(), fe),
                        HashMap::putAll);
    }

    @SuppressWarnings("unchecked")
    public <I, O> FlowElement<I, O> getFlowElement(FlowElementId id) {
        notNull(id, () -> "FlowElementId cant be null");
        try {
            return (FlowElement<I, O>) Optional.ofNullable(flowElementMap.get(id))
                    .orElseThrow(() -> new IllegalArgumentException("Can't find Flow Element by id"));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Not right generic types");
        }
    }
}
