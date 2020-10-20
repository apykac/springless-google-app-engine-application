package com.roundrobin_assignment.ticketpipeline.server;

import com.sun.net.httpserver.HttpHandler;

public interface EntryPoint {
    String path();

    HttpHandler httpHandler();
}
