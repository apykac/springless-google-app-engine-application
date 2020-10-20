package com.roundrobin_assignment.ticketpipeline.flow;

import com.roundrobin_assignment.ticketpipeline.clean.Cleanable;

public interface Flow extends Runnable {
    FlowId getFlowId();

    default void clean(Cleanable... cleanables) {
        if (cleanables != null && cleanables.length != 0) {
            for (int i = 0; i < cleanables.length; i++) {
                cleanables[i].clean();
                cleanables[i] = null;
            }
        }
    }
}
