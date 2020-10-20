package com.roundrobin_assignment.ticketpipeline.flow;


import com.roundrobin_assignment.ticketpipeline.domain.QWork;
import com.roundrobin_assignment.ticketpipeline.domain.TicketListHandleReport;
import com.roundrobin_assignment.ticketpipeline.flow.element.FlowElement;
import com.roundrobin_assignment.ticketpipeline.flow.element.FlowElementId;
import com.roundrobin_assignment.ticketpipeline.flow.element.FlowElementStore;
import com.roundrobin_assignment.ticketpipeline.task.WorkedFlowManager;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

import static com.roundrobin_assignment.ticketpipeline.flow.FlowId.GET_Q_QUEUE_HANDLER;

public class GetQQueueHandlerFlow implements Flow {
    private static final Logger LOG = LoggerFactory.getLogger(GetQQueueHandlerFlow.class);

    private final FlowElement<QWork, TicketListHandleReport> getTicketListZenDesk;
    private final FlowElement<TicketListHandleReport, TicketListHandleReport> processTicketListZenDesk;
    private final FlowElement<TicketListHandleReport, TicketListHandleReport> getQWorkCompleteZenDesk;
    private final WorkedFlowManager workedFlowManager;

    private QWork qWork;

    public GetQQueueHandlerFlow(FlowElementStore flowElementStore, WorkedFlowManager workedFlowManager, QWork qWork) {
        this.getTicketListZenDesk = flowElementStore.getFlowElement(FlowElementId.GET_TICKET_LIST_ZEN_DESK);
        this.processTicketListZenDesk = flowElementStore.getFlowElement(FlowElementId.PROCESS_TICKET_LIST_ZEN_DESK);
        this.getQWorkCompleteZenDesk = flowElementStore.getFlowElement(FlowElementId.GET_Q_WORK_COMPLETE_ZEN_DESK);
        this.workedFlowManager = workedFlowManager;
        this.qWork = qWork;
    }

    @Override
    public void run() {
        try {
            LOG.debug("Start GET_TICKET_LIST_ZEN_DESK flowStep");

            TicketListHandleReport getTicketListZenDeskResult = getTicketListZenDesk.doFlowStep(qWork);

            LOG.debug("End GET_TICKET_LIST_ZEN_DESK flowStep");
            LOG.debug("Start PROCESS_TICKET_LIST_ZEN_DESK flowStep");

            TicketListHandleReport processTicketListZenDeskResult = processTicketListZenDesk.doFlowStep(getTicketListZenDeskResult);

            LOG.debug("End PROCESS_TICKET_LIST_ZEN_DESK flowStep");
            LOG.debug("Start GET_Q_WORK_COMPLETE_ZEN_DESK flowStep");

            TicketListHandleReport getQWorkCompleteZenDeskResult = getQWorkCompleteZenDesk.doFlowStep(processTicketListZenDeskResult);

            LOG.debug("End GET_Q_WORK_COMPLETE_ZEN_DESK flowStep: {}", () -> getQWorkCompleteZenDeskResult);

            clean(getQWorkCompleteZenDeskResult);
            qWork = null;
            workedFlowManager.removeFlow(getFlowId());
        } catch (Exception e) {
            workedFlowManager.removeFlow(getFlowId());
        }
    }

    @Override
    public FlowId getFlowId() {
        return GET_Q_QUEUE_HANDLER;
    }
}
