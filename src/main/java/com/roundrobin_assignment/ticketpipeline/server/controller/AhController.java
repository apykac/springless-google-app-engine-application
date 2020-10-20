package com.roundrobin_assignment.ticketpipeline.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roundrobin_assignment.ticketpipeline.config.Context;

public class AhController extends AbstractController {
    public AhController(ObjectMapper objectMapper) {
        super(objectMapper);

        hello();
        ahStart();
        ahStop();
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
                    Context.close();
                } finally {
                    System.exit(0);
                }
            }).start();
            return null;
        });
    }
}
