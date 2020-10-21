package com.ticket_pipeline.simple_exchange.dao;

import com.ticket_pipeline.simple_exchange.domain.QWork;
import com.ticket_pipeline.simple_exchange.exception.JdbcException;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public interface QueueDao {
    List<QWork> getTasks();

    void processTicket(BigInteger getId,
                       LocalDateTime dueTime,
                       Integer siteId,
                       Integer queueId,
                       Long ticketId,
                       String ticketTags,
                       Long ticketGroupId,
                       Long ticketRequesterId,
                       Long ticketAssigneeId,
                       String ticketStatus,
                       Integer procType,
                       Integer getQType) throws JdbcException;

    void getQWorkComplete(BigInteger id,
                          int cnt,
                          int cntErr,
                          String nextPageUrl,
                          int status,
                          Integer errorCode,
                          String errorMsg) throws JdbcException;
}
