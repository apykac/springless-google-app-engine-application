package com.ticket_pipeline.simple_exchange.net.client;

import java.io.IOException;
import java.util.Map;

public interface RestOperations {
    <T> ResponseEntity<T> getToObjectBasicAuth(String url, String username, String password, Class<T> responseType) throws IOException, InterruptedException;

    <T> ResponseEntity<T> getToObject(String url, Class<T> responseType, Map<String, String> headers) throws IOException, InterruptedException;
}
