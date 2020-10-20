package com.roundrobin_assignment.ticketpipeline;

import com.roundrobin_assignment.ticketpipeline.config.Context;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

/**
 * Hello world!
 */
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting setup application...");
        Context.init();
        LOGGER.info("Application is started");
    }
}
