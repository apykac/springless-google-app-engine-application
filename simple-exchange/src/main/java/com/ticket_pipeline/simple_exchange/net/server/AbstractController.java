package com.ticket_pipeline.simple_exchange.net.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractController implements Controller {
    private final ObjectMapper objectMapper;
    private final List<EntryPoint> entryPoints = new LinkedList<>();

    public AbstractController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected <Rs> void getEntryPoint(String path, ResponseHandler<Rs, Void> responseHandler) {
        entryPoints.add(new EntryPoint() {
            @Override
            public String path() {
                return path;
            }

            @Override
            public HttpHandler httpHandler() {
                return exchange -> {
                    try {
                        if (isAllowedMethod(exchange, "GET")) {
                            Rs response = responseHandler.handleRequest(null, queryToMap(exchange.getRequestURI()));
                            createSuccessResponse(response, exchange);
                        }
                    } finally {
                        exchange.close();
                    }
                };
            }
        });
    }

    protected <Rs, Rq> void postEntryPoint(String path, ResponseHandler<Rs, Rq> responseHandler, Class<Rq> requestType) {
        entryPoints.add(new EntryPoint() {
            @Override
            public String path() {
                return path;
            }

            @Override
            public HttpHandler httpHandler() {
                return exchange -> {
                    try {
                        if (isAllowedMethod(exchange, "POST")) {
                            Rq request = objectMapper.readValue(exchange.getRequestBody(), requestType);
                            Rs response = responseHandler.handleRequest(request, queryToMap(exchange.getRequestURI()));
                            createSuccessResponse(response, exchange);
                        }
                    } finally {
                        exchange.close();
                    }
                };
            }
        });
    }

    private boolean isAllowedMethod(HttpExchange exchange, String method) throws IOException {
        if (!Objects.equals(method, exchange.getRequestMethod())) {
            notAllowedMethod(exchange, method);
            return false;
        } else {
            return true;
        }
    }

    private void notAllowedMethod(HttpExchange exchange, String method) throws IOException {
        createResponse("method " + method + "not allowed", 405, exchange);
    }

    private <Rs> void createSuccessResponse(Rs response, HttpExchange exchange) throws IOException {
        createResponse(response, 200, exchange);
    }

    private Map<String, String> queryToMap(URI uri) {
        String query = uri.getQuery();
        if (query == null) {
            return Collections.emptyMap();
        }
        String[] params = uri.getQuery().split("&");
        Map<String, String> result = new HashMap<>(params.length, 1);
        for (String param : params) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private <T> void createResponse(T response, int code, HttpExchange exchange) throws IOException {
        byte[] responseArr = (response instanceof String) ? ((String) response).getBytes() : objectMapper.writeValueAsBytes(response);
        exchange.sendResponseHeaders(code, responseArr.length);
        OutputStream output = exchange.getResponseBody();
        output.write(responseArr);
        output.flush();
    }

    @Override
    public List<EntryPoint> entryPoints() {
        return entryPoints;
    }

    protected interface ResponseHandler<Rs, Rq> {
        Rs handleRequest(Rq request, Map<String, String> params);
    }
}
