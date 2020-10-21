package com.ticket_pipeline.simple_exchange.flow.element;

import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_exchange.dao.QueueDao;
import com.ticket_pipeline.simple_exchange.domain.TicketListHandleReport;
import com.ticket_pipeline.simple_utils.StringUtils;
import com.ticket_pipeline.simple_utils.log.Logger;
import com.ticket_pipeline.simple_utils.log.LoggerFactory;

@Component
public class GetQWorkCompleteZenDeskFlowElement implements FlowElement<TicketListHandleReport, TicketListHandleReport> {
    private static final Logger LOG = LoggerFactory.getLogger(GetQWorkCompleteZenDeskFlowElement.class);

    private final QueueDao queueDao;

    @Constructor
    public GetQWorkCompleteZenDeskFlowElement(QueueDao queueDao) {
        this.queueDao = queueDao;
    }

    @Override
    public FlowElementId getFlowElementId() {
        return FlowElementId.GET_Q_WORK_COMPLETE_ZEN_DESK;
    }

    @Override
    public TicketListHandleReport doFlowStep(TicketListHandleReport income) {
        if (income != null) {
            getQWorkComplete(income);
        }
        return income;
    }

    private void getQWorkComplete(TicketListHandleReport report) {
        try {
            queueDao.getQWorkComplete(
                    report.getId(),
                    report.getProcessTicketCount(),
                    report.getProcessTicketErrors(),
                    report.getNextPage(),
                    report.getStatus().getValue(),
                    report.getErrorCode(),
                    StringUtils.left(report.getErrorMessage(), 255));
        } catch (Exception e) {
            LOG.error("Error during call pGetQWorkComplite(): {}", () -> e);
            LOG.trace("Error during call pGetQWorkComplite(): entity: {}", () -> report);
        }
    }
}
