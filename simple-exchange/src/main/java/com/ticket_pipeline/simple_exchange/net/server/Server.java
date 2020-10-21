package com.ticket_pipeline.simple_exchange.net.server;

import com.sun.net.httpserver.HttpServer;
import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_context.Destroy;
import com.ticket_pipeline.simple_context.Init;
import com.ticket_pipeline.simple_utils.Environment;
import com.ticket_pipeline.simple_utils.log.Logger;
import com.ticket_pipeline.simple_utils.log.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

@Component
public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private final HttpServer innerServer;
    private final List<Controller> controllers;

    @Constructor
    public Server(List<Controller> controllers) throws IOException {
        int serverPort = Environment.getProp("PORT", 8080, Integer.class);
        innerServer = HttpServer.create(new InetSocketAddress(serverPort), 0);
        this.controllers = controllers;
    }

    @Init
    public void start() {
        if (controllers != null && !controllers.isEmpty()) {
            controllers.forEach(controller -> registerEntryPoints(controller.entryPoints()));
        }
        innerServer.start();
    }

    private void registerEntryPoints(List<EntryPoint> entryPointList) {
        if (entryPointList != null && !entryPointList.isEmpty()) {
            entryPointList.forEach(ep -> {
                innerServer.createContext(ep.path(), ep.httpHandler());
                LOG.info("Register context path: {}", ep::path);
            });
        }
    }

    @Destroy
    public void stop() {
        innerServer.stop(1);
    }
}
