package com.roundrobin_assignment.ticketpipeline;

import com.roundrobin_assignment.ticketpipeline.config.context.Context;
import com.roundrobin_assignment.ticketpipeline.exception.InitContextRuntimeException;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

/**
 * Hello world!
 */
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) {
        LOG.info("Starting setup application...");
        try {
            Context.init(Application.class.getPackageName());
        } catch (InitContextRuntimeException e) {
            LOG.error("Exception during init context: {}", e::getMessage, () -> e);
            com.roundrobin_assignment.ticketpipeline.config.context.Context.stop();
            throw e;
        }
        LOG.info("Application is started");
    }
}
