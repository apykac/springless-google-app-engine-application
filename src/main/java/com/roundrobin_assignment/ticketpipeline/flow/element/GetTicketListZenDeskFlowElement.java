package com.roundrobin_assignment.ticketpipeline.flow.element;

import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.domain.QWork;
import com.roundrobin_assignment.ticketpipeline.domain.TaskStatus;
import com.roundrobin_assignment.ticketpipeline.domain.TicketList;
import com.roundrobin_assignment.ticketpipeline.domain.TicketListHandleReport;
import com.roundrobin_assignment.ticketpipeline.net.ResponseEntity;
import com.roundrobin_assignment.ticketpipeline.net.RestOperations;
import com.roundrobin_assignment.ticketpipeline.util.CryptUtil;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

@Component
public class GetTicketListZenDeskFlowElement implements FlowElement<QWork, TicketListHandleReport> {
    private static final Logger LOG = LoggerFactory.getLogger(GetTicketListZenDeskFlowElement.class);

    private final RestOperations zenDeskRestClient;

    @Constructor
    public GetTicketListZenDeskFlowElement(RestOperations zenDeskRestClient) {
        this.zenDeskRestClient = zenDeskRestClient;
    }

    @Override
    public FlowElementId getFlowElementId() {
        return FlowElementId.GET_TICKET_LIST_ZEN_DESK;
    }

    @Override
    public TicketListHandleReport doFlowStep(QWork qWork) {
        return qWork == null ? null : getTicketList(qWork);
    }

    private TicketListHandleReport getTicketList(QWork qWork) {
        String url = qWork.getUrl();
        try {
            String user = qWork.getUser();
            String pwd = CryptUtil.decode(qWork.getPwd());
            LOG.trace("Start call url: {}", () -> url);
            ResponseEntity<TicketList> response = zenDeskRestClient.getToObjectBasicAuth(url, user, pwd, TicketList.class);
            return fromResponse(response, qWork);
        } catch (Exception e) {
            LOG.error("Error during call ZenDesk url: {}, cause: {}", () -> url, e::getMessage, () -> e);
            return fromException(e, qWork);
        }
    }

    private TicketListHandleReport fromResponse(ResponseEntity<TicketList> response, QWork qWork) {
        TicketListHandleReport result = new TicketListHandleReport().setGetTask(qWork);
        if (response.getCode() / 100 != 2) {
            return result
                    .setErrorCode(response.getCode())
                    .setStatus(TaskStatus.ERROR);
        }
        return result.setTicketList(response.getBody());
    }

    private TicketListHandleReport fromException(Exception e, QWork qWork) {
        return new TicketListHandleReport()
                .setGetTask(qWork)
                .setStatus(TaskStatus.ERROR)
                .setErrorMessage(e.getMessage());
    }
}
