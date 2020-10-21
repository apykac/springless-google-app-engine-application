package com.ticket_pipeline.simple_exchange.flow;


import com.ticket_pipeline.simple_exchange.domain.QWork;
import com.ticket_pipeline.simple_exchange.domain.TicketListHandleReport;
import com.ticket_pipeline.simple_exchange.flow.element.FlowElement;
import com.ticket_pipeline.simple_exchange.flow.element.FlowElementId;
import com.ticket_pipeline.simple_exchange.flow.element.FlowElementStore;
import com.ticket_pipeline.simple_exchange.task.GetQWorkQueue;
import com.ticket_pipeline.simple_utils.clean.Cleanable;
import com.ticket_pipeline.simple_utils.log.Logger;
import com.ticket_pipeline.simple_utils.log.LoggerFactory;

public class GetQQueueHandlerFlow extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(GetQQueueHandlerFlow.class);

    private final FlowElement<QWork, TicketListHandleReport> getTicketListZenDesk;
    private final FlowElement<TicketListHandleReport, TicketListHandleReport> processTicketListZenDesk;
    private final FlowElement<TicketListHandleReport, TicketListHandleReport> getQWorkCompleteZenDesk;
    private final GetQWorkQueue getQWorkQueue;

    private boolean stop = false;
    private boolean isFinished = false;

    public GetQQueueHandlerFlow(FlowElementStore flowElementStore, GetQWorkQueue getQWorkQueue) {
        this.getTicketListZenDesk = flowElementStore.getFlowElement(FlowElementId.GET_TICKET_LIST_ZEN_DESK);
        this.processTicketListZenDesk = flowElementStore.getFlowElement(FlowElementId.PROCESS_TICKET_LIST_ZEN_DESK);
        this.getQWorkCompleteZenDesk = flowElementStore.getFlowElement(FlowElementId.GET_Q_WORK_COMPLETE_ZEN_DESK);
        this.getQWorkQueue = getQWorkQueue;
    }

    @Override
    public void run() {
        QWorkWrapper qWorkWrapper = new QWorkWrapper();
        while (!stop) {
            try {
                LOG.debug("Start retrieve QWork flowStep");

                qWorkWrapper.qWork = getQWorkQueue.getQWork();

                LOG.debug("Start GET_TICKET_LIST_ZEN_DESK flowStep");

                TicketListHandleReport getTicketListZenDeskResult = getTicketListZenDesk.doFlowStep(qWorkWrapper.qWork);

                LOG.debug("End GET_TICKET_LIST_ZEN_DESK flowStep");
                LOG.debug("Start PROCESS_TICKET_LIST_ZEN_DESK flowStep");

                TicketListHandleReport processTicketListZenDeskResult = processTicketListZenDesk.doFlowStep(getTicketListZenDeskResult);

                LOG.debug("End PROCESS_TICKET_LIST_ZEN_DESK flowStep");
                LOG.debug("Start GET_Q_WORK_COMPLETE_ZEN_DESK flowStep");

                TicketListHandleReport getQWorkCompleteZenDeskResult = getQWorkCompleteZenDesk.doFlowStep(processTicketListZenDeskResult);

                LOG.debug("End GET_Q_WORK_COMPLETE_ZEN_DESK flowStep");
                LOG.trace("after GET_Q_WORK_COMPLETE_ZEN_DESK flowStep: {}", () -> getQWorkCompleteZenDeskResult);

                clean(getQWorkCompleteZenDeskResult);
            } catch (Exception e) {
                LOG.error("Error during GetQQueueHandlerFlow: entity: {}; exception: {}", () -> qWorkWrapper.qWork, () -> e);
            }
        }
        isFinished = true;
    }

    private void clean(Cleanable... cleanables) {
        if (cleanables != null && cleanables.length != 0) {
            for (int i = 0; i < cleanables.length; i++) {
                cleanables[i].clean();
                cleanables[i] = null;
            }
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void interrupt() {
        stop = true;
    }

    /**
     * for logging purpose
     */
    private static class QWorkWrapper {
        private QWork qWork;
    }
}
