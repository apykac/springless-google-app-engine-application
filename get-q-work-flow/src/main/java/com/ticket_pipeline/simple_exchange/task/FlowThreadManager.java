package com.ticket_pipeline.simple_exchange.task;

import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_context.Destroy;
import com.ticket_pipeline.simple_context.Init;
import com.ticket_pipeline.simple_exchange.flow.GetQQueueHandlerFlow;
import com.ticket_pipeline.simple_exchange.flow.element.FlowElementStore;
import com.ticket_pipeline.simple_utils.Environment;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlowThreadManager {
    private final FlowElementStore flowElementStore;
    private final GetQWorkQueue getQWorkQueue;

    private int threadCount;
    private List<GetQQueueHandlerFlow> threads;

    @Constructor
    public FlowThreadManager(FlowElementStore flowElementStore, GetQWorkQueue getQWorkQueue) {
        this.flowElementStore = flowElementStore;
        this.getQWorkQueue = getQWorkQueue;
        threadCount = Environment.getProp("app.thread-count", 4, Integer.class);
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int resizeThreadCount(int newThreadCount) {
        if (newThreadCount != threadCount && newThreadCount > 0) {
            destroy(threads);
            threadCount = newThreadCount;
            init();
        }
        return threadCount;
    }

    @Init(-2)
    public void init() {
        threads = new ArrayList<>(threadCount);
        for (int i = 0; i < threadCount; i++) {
            GetQQueueHandlerFlow flow = new GetQQueueHandlerFlow(flowElementStore, getQWorkQueue);
            threads.add(flow);
            flow.start();
        }
    }

    @Destroy(2)
    public void destroy() {
        destroy(threads);
        threads.clear();
    }

    public void destroy(List<GetQQueueHandlerFlow> incomeThreads) {
        incomeThreads.forEach(Thread::interrupt);
        int count = 0;
        while (count != 10 && !isAllStop()) {
            pause(1000);
            count++;
        }
    }

    private boolean isAllStop() {
        return threads.stream().allMatch(GetQQueueHandlerFlow::isFinished);
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //nothing
        }
    }
}
