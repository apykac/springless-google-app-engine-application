package com.ticket_pipeline.simple_exchange;

import com.ticket_pipeline.simple_context.Context;
import com.ticket_pipeline.simple_utils.log.Logger;
import com.ticket_pipeline.simple_utils.log.LoggerFactory;

public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        LOG.info("Starting setup application...");
        Context.init(Application.class.getPackageName());
        LOG.info("Application is started");
    }
}
