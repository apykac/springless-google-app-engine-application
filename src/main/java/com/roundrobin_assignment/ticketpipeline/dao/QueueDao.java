package com.roundrobin_assignment.ticketpipeline.dao;

import com.roundrobin_assignment.ticketpipeline.domain.QWork;

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
                       Integer getQType);

    void getQWorkComplete(BigInteger id,
                          int cnt,
                          int cntErr,
                          String nextPageUrl,
                          int status,
                          Integer errorCode,
                          String errorMsg);
}
