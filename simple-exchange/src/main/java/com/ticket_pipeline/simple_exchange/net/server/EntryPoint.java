package com.ticket_pipeline.simple_exchange.net.server;

import com.sun.net.httpserver.HttpHandler;

public interface EntryPoint {
    String path();

    HttpHandler httpHandler();
}
