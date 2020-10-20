package com.roundrobin_assignment.ticketpipeline.server;

import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.config.context.Destroy;
import com.roundrobin_assignment.ticketpipeline.config.context.Environment;
import com.roundrobin_assignment.ticketpipeline.config.context.Init;
import com.roundrobin_assignment.ticketpipeline.server.controller.Controller;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;
import com.sun.net.httpserver.HttpServer;

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
