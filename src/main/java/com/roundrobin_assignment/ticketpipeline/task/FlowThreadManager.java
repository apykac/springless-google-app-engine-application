package com.roundrobin_assignment.ticketpipeline.task;

import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.config.context.Destroy;
import com.roundrobin_assignment.ticketpipeline.config.context.Environment;
import com.roundrobin_assignment.ticketpipeline.config.context.Init;
import com.roundrobin_assignment.ticketpipeline.flow.GetQQueueHandlerFlow;
import com.roundrobin_assignment.ticketpipeline.flow.element.FlowElementStore;
import com.roundrobin_assignment.ticketpipeline.jdbc.datacource.DataSource;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlowThreadManager {
    private final FlowElementStore flowElementStore;
    private final GetQWorkQueue getQWorkQueue;
    private final DataSource dataSource;

    private int threadCount;
    private List<GetQQueueHandlerFlow> threads;

    @Constructor
    public FlowThreadManager(FlowElementStore flowElementStore, GetQWorkQueue getQWorkQueue, DataSource dataSource) {
        this.flowElementStore = flowElementStore;
        this.getQWorkQueue = getQWorkQueue;
        this.dataSource = dataSource;

        resizeTreadCount(Environment.getProp("app.thread-count", 4, Integer.class));
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int setThreadCount(int newThreadCount) {
        if (newThreadCount != threadCount && newThreadCount > 0) {
            destroy(threads);
            dataSource.resizeTreadPool(newThreadCount);
            threads.clear();
            resizeTreadCount(newThreadCount);
            init();
        }
        return threadCount;
    }

    private void resizeTreadCount(int newThreadCount) {
        threadCount = newThreadCount;
        threads = new ArrayList<>(newThreadCount);
        for (int i = 0; i < threadCount; i++) {
            threads.add(new GetQQueueHandlerFlow(flowElementStore, getQWorkQueue));
        }
    }

    @Init(-2)
    public void init() {
        threads.forEach(Thread::start);
    }

    @Destroy(2)
    public void destroy() {
        destroy(threads);
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
