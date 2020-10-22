package com.ticket_pipeline.simple_exchange.flow.element;

import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_exchange.domain.QWork;
import com.ticket_pipeline.simple_exchange.domain.TaskStatus;
import com.ticket_pipeline.simple_exchange.domain.TicketList;
import com.ticket_pipeline.simple_exchange.domain.TicketListHandleReport;
import com.ticket_pipeline.simple_exchange.net.client.ResponseEntity;
import com.ticket_pipeline.simple_exchange.net.client.RestOperations;
import com.ticket_pipeline.simple_utils.CryptUtil;
import com.ticket_pipeline.simple_utils.StringUtils;
import com.ticket_pipeline.simple_utils.log.Logger;
import com.ticket_pipeline.simple_utils.log.LoggerFactory;

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
    public TicketListHandleReport doFlowStep(QWork income) {
        return income == null ? null : getTicketList(income);
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
            LOG.error("Error during call ZenDesk url: {}, cause: {}", () -> url, () -> e);
            return fromException(e, qWork);
        }
    }

    private TicketListHandleReport fromResponse(ResponseEntity<TicketList> response, QWork qWork) {
        TicketListHandleReport result = new TicketListHandleReport().setGetTask(qWork);
        if (!response.isOk()) {
            LOG.error("ZenDesk return: {} code", response::getCode);
            return result
                    .setErrorCode(response.getCode())
                    .setErrorMessage(response.getBody().getError())
                    .setStatus(TaskStatus.ERROR);
        }
        return result.setTicketList(response.getBody());
    }

    private TicketListHandleReport fromException(Exception e, QWork qWork) {
        return new TicketListHandleReport()
                .setGetTask(qWork)
                .setStatus(TaskStatus.ERROR)
                .setErrorMessage(StringUtils.exceptionToString(e));
    }
}
