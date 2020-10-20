package com.roundrobin_assignment.ticketpipeline.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.config.context.Context;
import com.roundrobin_assignment.ticketpipeline.config.context.Init;

@Component
public class AhController extends AbstractController {
    @Constructor
    public AhController(ObjectMapper objectMapper) {
        super(objectMapper);
//        init();
    }

    void hello() {
        getEntryPoint("/", (request, params) -> "{\"response\", \"There is ticket pipeline application!\"}");
    }

    void ahStart() {
        getEntryPoint("/_ah/start", (request, params) -> "{\"response\", \"you call start!\"}");
    }

    void ahStop() {
        getEntryPoint("/_ah/stop", (request, params) -> {
            new Thread(() -> {
                try {
                    Context.stop();
                } finally {
                    System.exit(0);
                }
            }).start();
            return null;
        });
    }

    @Init(1)
    public void init() {
        hello();
        ahStart();
        ahStop();
    }
}
