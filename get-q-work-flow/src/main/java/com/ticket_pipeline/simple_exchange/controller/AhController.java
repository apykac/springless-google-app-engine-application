package com.ticket_pipeline.simple_exchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_context.Context;
import com.ticket_pipeline.simple_context.Init;
import com.ticket_pipeline.simple_exchange.net.server.AbstractController;

@Component
public class AhController extends AbstractController {
    @Constructor
    public AhController(ObjectMapper objectMapper) {
        super(objectMapper);
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
