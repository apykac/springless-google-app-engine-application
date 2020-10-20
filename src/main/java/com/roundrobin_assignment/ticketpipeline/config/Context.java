package com.roundrobin_assignment.ticketpipeline.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.roundrobin_assignment.ticketpipeline.config.context.Environment;
import com.roundrobin_assignment.ticketpipeline.dao.QueueDao;
import com.roundrobin_assignment.ticketpipeline.dao.QueueDaoImpl;
import com.roundrobin_assignment.ticketpipeline.domain.QWork;
import com.roundrobin_assignment.ticketpipeline.domain.TicketListHandleReport;
import com.roundrobin_assignment.ticketpipeline.exception.InitContextRuntimeException;
import com.roundrobin_assignment.ticketpipeline.flow.element.FlowElement;
import com.roundrobin_assignment.ticketpipeline.flow.element.FlowElementStore;
import com.roundrobin_assignment.ticketpipeline.flow.element.GetQWorkCompleteZenDeskFlowElement;
import com.roundrobin_assignment.ticketpipeline.flow.element.GetTicketListZenDeskFlowElement;
import com.roundrobin_assignment.ticketpipeline.flow.element.ProcessTicketListZenDeskFlowElement;
import com.roundrobin_assignment.ticketpipeline.jdbc.JdbcOperations;
import com.roundrobin_assignment.ticketpipeline.jdbc.JdbcOperationsImpl;
import com.roundrobin_assignment.ticketpipeline.jdbc.datacource.DataSource;
import com.roundrobin_assignment.ticketpipeline.net.RestClient;
import com.roundrobin_assignment.ticketpipeline.net.RestOperations;
import com.roundrobin_assignment.ticketpipeline.server.Server;
import com.roundrobin_assignment.ticketpipeline.server.controller.AhController;
import com.roundrobin_assignment.ticketpipeline.server.controller.MainController;
import com.roundrobin_assignment.ticketpipeline.task.GetQueueTask;
import com.roundrobin_assignment.ticketpipeline.task.ScheduledTaskManager;
import com.roundrobin_assignment.ticketpipeline.task.WorkedFlowManager;
import com.roundrobin_assignment.ticketpipeline.util.log.LogLevel;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

import java.util.Arrays;

public class Context {
    private static final Logger LOG = LoggerFactory.getLogger(Context.class);

    private static final DataSource DATA_SOURCE;
    private static final JdbcOperations JDBC_OPERATIONS;
    private static final QueueDao QUEUE_DAO;
    private static final ObjectMapper OBJECT_MAPPER;
    private static final Server SERVER;
    private static final RestOperations REST_OPERATIONS;
    private static final FlowElementStore FLOW_ELEMENT_STORE;
    private static final WorkedFlowManager WORKED_FLOW_MANAGER;
    private static final ScheduledTaskManager SCHEDULED_TASK_MANAGER;

    static {
        try {
            LOG.info("Starting init data source");
            DATA_SOURCE = new DataSource();

            LOG.info("Starting init object mapper");
            OBJECT_MAPPER = new ObjectMapper();
            OBJECT_MAPPER.registerModule(new JavaTimeModule());
            OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            LOG.info("Starting init dao layer");
            JDBC_OPERATIONS = new JdbcOperationsImpl(getDataSource());

            QUEUE_DAO = new QueueDaoImpl(getJdbcOperations());

            LOG.info("Starting init server layer");
            SERVER = new Server();
            SERVER.registerControllers(Arrays.asList(
                    new AhController(getObjectMapper()),
                    new MainController(getObjectMapper(), new GetQueueTask(getQueueDao(), getFlowElementStore(), getWorkedFlowManager()))));
            SERVER.start();

            REST_OPERATIONS = new RestClient(getObjectMapper());

            LoggerFactory.setLogLevel(Environment.getProp("LogLevel", LogLevel.TRACE, LogLevel.class));

            LOG.info("Starting init flows layer");
            LOG.debug("Starting init GetQWorkCompleteZenDeskFlowElement");
            FlowElement<TicketListHandleReport, TicketListHandleReport> getQWorkCompleteZenDeskFlowElement = new GetQWorkCompleteZenDeskFlowElement(getQueueDao());

            LOG.debug("Starting init GetTicketListZenDeskFlowElement");
            FlowElement<QWork, TicketListHandleReport> getTicketListZenDeskFlowElement = new GetTicketListZenDeskFlowElement(getRestOperations());

            LOG.debug("Starting init ProcessTicketListZenDeskFlowElement");
            FlowElement<TicketListHandleReport, TicketListHandleReport> processTicketListZenDeskFlowElement = new ProcessTicketListZenDeskFlowElement(getQueueDao());


            FLOW_ELEMENT_STORE = new FlowElementStore(Arrays.asList(
                    getQWorkCompleteZenDeskFlowElement,
                    getTicketListZenDeskFlowElement,
                    processTicketListZenDeskFlowElement
            ));

            LOG.debug("Starting init WorkedFlowManager");
            WORKED_FLOW_MANAGER = new WorkedFlowManager();

            LOG.debug("Starting init ScheduledTaskManager");
            SCHEDULED_TASK_MANAGER = new ScheduledTaskManager(Arrays.asList(
                    new GetQueueTask(getQueueDao(), getFlowElementStore(), getWorkedFlowManager())
            ));

            LOG.info("Context has been successfully initialized");
        } catch (Exception e) {
            LOG.error("Exception during init context: {}", e::getMessage, () -> e);
            throw new InitContextRuntimeException(e);
        }
    }

    private Context() {
    }

    public static void init() {
        //dummy need to start init context
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    public static JdbcOperations getJdbcOperations() {
        return JDBC_OPERATIONS;
    }

    public static QueueDao getQueueDao() {
        return QUEUE_DAO;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static Server getServer() {
        return SERVER;
    }

    public static RestOperations getRestOperations() {
        return REST_OPERATIONS;
    }

    public static FlowElementStore getFlowElementStore() {
        return FLOW_ELEMENT_STORE;
    }

    public static WorkedFlowManager getWorkedFlowManager() {
        return WORKED_FLOW_MANAGER;
    }

    public static void close() {
        SCHEDULED_TASK_MANAGER.destroy();
        SERVER.stop();
    }
}
