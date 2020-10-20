package com.roundrobin_assignment.ticketpipeline.task;

import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.config.context.Destroy;
import com.roundrobin_assignment.ticketpipeline.config.context.Environment;
import com.roundrobin_assignment.ticketpipeline.dao.QueueDao;
import com.roundrobin_assignment.ticketpipeline.domain.QWork;
import com.roundrobin_assignment.ticketpipeline.flow.FlowId;
import com.roundrobin_assignment.ticketpipeline.flow.GetQQueueHandlerFlow;
import com.roundrobin_assignment.ticketpipeline.flow.element.FlowElementStore;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.roundrobin_assignment.ticketpipeline.flow.FlowId.GET_Q_QUEUE_HANDLER;
import static com.roundrobin_assignment.ticketpipeline.util.CollectionUtils.notEmpty;

@Component
public class GetQueueTask implements Task {
    private static final Logger LOG = LoggerFactory.getLogger(GetQueueTask.class);
    private static final FlowId FLOW_ID = GET_Q_QUEUE_HANDLER;
    private static final String GET_Q_QUEUE_HANDLER_THREAD_COUNT = FLOW_ID.name() + ".thread-count";

    private final Queue<QWork> qWorkQueue = new ConcurrentLinkedDeque<>();

    private final QueueDao queueDao;
    private final FlowElementStore flowElementStore;
    private final WorkedFlowManager workedFlowManager;
    private final ExecutorService executorService;

    private boolean isStop = false;


    @Constructor
    public GetQueueTask(QueueDao queueDao, FlowElementStore flowElementStore, WorkedFlowManager workedFlowManager) {
        this.queueDao = queueDao;
        this.flowElementStore = flowElementStore;
        this.workedFlowManager = workedFlowManager;
        executorService = Executors.newFixedThreadPool(Environment.getProp(GET_Q_QUEUE_HANDLER_THREAD_COUNT, 1, Integer.class));
    }

    @Override
    public void run() {
        LOG.trace("Run GetQueueTask");
        QWork qWork;
        while (!workedFlowManager.isMax(GET_Q_QUEUE_HANDLER) && (qWork = getQWork()) != null && !isStop) {
            executorService.execute(new GetQQueueHandlerFlow(flowElementStore, workedFlowManager.addFlow(GET_Q_QUEUE_HANDLER), qWork));
        }
    }

    private QWork getQWork() {
        QWork qWork = qWorkQueue.poll();
        if (qWork == null) {
            fillQWorkQueue();
            qWork = qWorkQueue.poll();
        }
        return qWork;
    }

    private void fillQWorkQueue() {
        List<QWork> qWorkList = queueDao.getTasks();
        if (notEmpty(qWorkList)) {
            qWorkQueue.addAll(qWorkList);
        }
    }

    @Override
    @Destroy
    public void destroy() {
        isStop = true;
        executorService.shutdown();
    }

    @Override
    public FlowId getFlowId() {
        return GET_Q_QUEUE_HANDLER;
    }
}
