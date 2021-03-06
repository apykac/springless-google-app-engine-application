package com.ticket_pipeline.simple_exchange.flow.element;

import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_exchange.dao.QueueDao;
import com.ticket_pipeline.simple_exchange.domain.QWork;
import com.ticket_pipeline.simple_exchange.domain.Status;
import com.ticket_pipeline.simple_exchange.domain.TaskStatus;
import com.ticket_pipeline.simple_exchange.domain.Ticket;
import com.ticket_pipeline.simple_exchange.domain.TicketListHandleReport;
import com.ticket_pipeline.simple_utils.StringUtils;
import com.ticket_pipeline.simple_utils.log.Logger;
import com.ticket_pipeline.simple_utils.log.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ProcessTicketListZenDeskFlowElement implements FlowElement<TicketListHandleReport, TicketListHandleReport> {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessTicketListZenDeskFlowElement.class);

    private final QueueDao queueDao;

    @Constructor
    public ProcessTicketListZenDeskFlowElement(QueueDao queueDao) {
        this.queueDao = queueDao;
    }

    @Override
    public FlowElementId getFlowElementId() {
        return FlowElementId.PROCESS_TICKET_LIST_ZEN_DESK;
    }

    @Override
    public TicketListHandleReport doFlowStep(TicketListHandleReport income) {
        if (income != null && Objects.equals(income.getStatus(), TaskStatus.OK)) {
            processTicketList(income);
        }
        return income;
    }

    private void processTicketList(TicketListHandleReport ticketListHandleReport) {
        List<Ticket> tickets = ticketListHandleReport.getTickets();
        if (tickets == null || tickets.isEmpty()) {
            LOG.trace("Tickets id empty in: {}", () -> ticketListHandleReport);
            return;
        }
        QWork qWork = ticketListHandleReport.getGetTask();
        boolean isQType = qWork.getGetQType() == 0;
        boolean isAllowReassign = Objects.equals(qWork.getAllowReassign(), Boolean.TRUE);
        TicketCounter counter = new TicketCounter();

        tickets.stream()
                .filter(ticket -> applyFilter(isQType, isAllowReassign, ticket))
                .forEach(ticket -> callProcessTicket(qWork, ticket, counter));

        ticketListHandleReport.setProcessTicketCount(counter.getSuccessCount());
        ticketListHandleReport.setProcessTicketErrors(counter.getErrorCount());
    }

    private boolean applyFilter(boolean isQType, boolean isAllowReassign, Ticket ticket) {
        if (isQType) {
            return !Objects.equals(ticket.getStatus(), Status.CLOSED) && (ticket.getAssigneeId() == null || isAllowReassign);
        } else {
            return !Objects.equals(ticket.getStatus(), Status.CLOSED) && !Objects.equals(ticket.getStatus(), Status.SOLVED);
        }
    }

    private void callProcessTicket(QWork qWork, Ticket ticket, TicketCounter ticketCounter) {
        try {
            int ticketNumber = ticketCounter.getAndIncrementTicketNumber();
            queueDao.processTicket(
                    qWork.getGetId(),
                    Optional.ofNullable(qWork.getProcStartTime()).map(t -> t.plusSeconds(ticketNumber * qWork.getProcSpeed() / 60)).orElse(null),
                    qWork.getSiteId(),
                    qWork.getQueueId(),
                    ticket.getId(),
                    (ticket.getTags() == null || ticket.getTags().isEmpty()) ? "[]" : String.format("[\"%s\"]", String.join("\",\"", ticket.getTags())),
                    ticket.getGroupId(),
                    ticket.getRequesterId(),
                    ticket.getAssigneeId(),
                    ticket.getStatus() == null ? null : StringUtils.left(ticket.getStatus().toString(), 64),
                    qWork.getProcType(),
                    qWork.getGetQType());
            ticketCounter.successTicket();
        } catch (Exception e) {
            LOG.error("Error during call pProcessTicket(): {}", () -> e);
            LOG.trace("Error during call pProcessTicket(): entity: {}, {}", () -> qWork, () -> ticket);
            ticketCounter.errorTicket();
        }
    }

    private static class TicketCounter {
        int ticketNumber = 0;
        int successCount = 0;
        int errorCount = 0;

        int getAndIncrementTicketNumber() {
            ticketNumber++;
            return ticketNumber;
        }

        void successTicket() {
            successCount++;
        }

        void errorTicket() {
            errorCount++;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getErrorCount() {
            return errorCount;
        }
    }
}
