package com.ticket_pipeline.simple_exchange.dao;

import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_exchange.domain.QWork;
import com.ticket_pipeline.simple_exchange.exception.JdbcException;
import com.ticket_pipeline.simple_exchange.jdbc.JdbcOperations;
import com.ticket_pipeline.simple_utils.log.Logger;
import com.ticket_pipeline.simple_utils.log.LoggerFactory;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QueueDaoImpl implements QueueDao {
    private static final Logger LOG = LoggerFactory.getLogger(QueueDaoImpl.class);

    private final JdbcOperations jdbcOperations;

    @Constructor
    public QueueDaoImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<QWork> getTasks() {
        try {
            LOG.trace("Start call pGetQWork()");

            List<QWork> qWorkList = jdbcOperations.query("call pGetQWork()", (rs, rowNum) -> {
                try {
                    return new QWork()
                            .setId(rs.getObject("id", BigInteger.class))
                            .setSiteId(rs.getObject("site_id", Integer.class))
                            .setQueueId(rs.getObject("queue_id", Integer.class))
                            .setGetId(rs.getObject("get_id", BigInteger.class))
                            .setUrl(rs.getObject("url", String.class))
                            .setAllowReassign(rs.getObject("allow_reassign", Boolean.class))
                            .setProcSpeed(rs.getObject("proc_speed", Integer.class))
                            .setProcStartTime(rs.getObject("proc_start_time", LocalDateTime.class))
                            .setProcType(rs.getObject("proc_type", Integer.class))
                            .setUser(rs.getObject("user", String.class))
                            .setPwd(rs.getObject("pwd", String.class))
                            .setGetQType(rs.getObject("get_q_type", Integer.class));
                } catch (Exception e) {
                    throw new SQLException("Can't parse date cause: " + e.getMessage(), e);
                }
            });
            LOG.trace("Result of pGetQWork(): {}", () -> qWorkList.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            return qWorkList;
        } catch (Exception e) {
            LOG.error("Error during call pGetQWork(): {}", () -> e);
            return Collections.emptyList();
        }
    }

    @Override
    public void processTicket(BigInteger getId,
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
                              Integer getQType) throws JdbcException {
        LOG.trace("Start call pProcessTicket(), with params: getId={}, dueTime={}, siteId={}, queueId={}, ticketId={}, " +
                        "ticketTags={}, ticketGroupId={}, ticketRequesterId={}, ticketAssigneeId={}, ticketStatus={}, procType={}, getQType={}",
                () -> getId, () -> dueTime, () -> siteId, () -> queueId, () -> ticketId, () -> ticketTags, () -> ticketGroupId,
                () -> ticketRequesterId, () -> ticketAssigneeId, () -> ticketStatus, () -> procType, () -> getQType);

        jdbcOperations.update("call pProcessTicket(?,?,?,?,?,?,?,?,?,?,?,?)",
                getId, dueTime, siteId, queueId, ticketId, ticketTags, ticketGroupId, ticketRequesterId, ticketAssigneeId, ticketStatus, procType, getQType);
    }

    @Override
    public void getQWorkComplete(BigInteger id,
                                 int cnt,
                                 int cntErr,
                                 String nextPageUrl,
                                 int status,
                                 Integer errorCode,
                                 String errorMsg) throws JdbcException {
        LOG.trace("Start call pGetQWorkComplite(), with params: id={}, cnt={}, cntErr={}, nextPageUrl={}, status={}, errorCode={}, errorMsg={}",
                () -> id, () -> cnt, () -> cntErr, () -> nextPageUrl, () -> status, () -> errorCode, () -> errorMsg);

        jdbcOperations.update("call pGetQWorkComplite(?,?,?,?,?,?,?)",
                id, cnt, cntErr, nextPageUrl, status, errorCode, errorMsg);
    }
}
