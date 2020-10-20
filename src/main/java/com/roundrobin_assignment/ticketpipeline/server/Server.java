package com.roundrobin_assignment.ticketpipeline.server;

import com.roundrobin_assignment.ticketpipeline.config.context.Environment;
import com.roundrobin_assignment.ticketpipeline.constants.Constants;
import com.roundrobin_assignment.ticketpipeline.server.controller.Controller;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private final HttpServer innerServer;

    public Server() throws IOException {
        int serverPort = Environment.getProp(Constants.PORT_ENV, 8080, Integer.class);
        innerServer = HttpServer.create(new InetSocketAddress(serverPort), 0);
    }

    public void registerControllers(List<Controller> controllers) {
        if (controllers != null && !controllers.isEmpty()) {
            controllers.forEach(controller -> registerEntryPoints(controller.entryPoints()));
        }
    }

    public void registerEntryPoints(List<EntryPoint> entryPointList) {
        if (entryPointList != null && !entryPointList.isEmpty()) {
            entryPointList.forEach(ep -> {
                innerServer.createContext(ep.path(), ep.httpHandler());
                LOG.info("Register context path: {}", ep::path);
            });
        }
    }

    public void start() {
        innerServer.start();
    }

    public void stop() {
        innerServer.stop(1);
    }
}
