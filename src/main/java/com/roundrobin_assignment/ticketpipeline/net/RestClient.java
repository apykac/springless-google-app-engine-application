package com.roundrobin_assignment.ticketpipeline.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class RestClient implements RestOperations {
    private final java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder().build();
    private final ObjectMapper objectMapper;

    @Constructor
    public RestClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> ResponseEntity<T> getToObjectBasicAuth(String url, String username, String password, Class<T> responseType) throws IOException, InterruptedException {
        return getToObject(
                url,
                responseType,
                Map.of("Authorization", "Basic " + Base64.getEncoder().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public <T> ResponseEntity<T> getToObject(String url, Class<T> responseType, Map<String, String> headers)
            throws IOException, InterruptedException {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
        if (CollectionUtils.notEmpty(headers)) {
            headers.forEach(httpRequestBuilder::header);
        }
        HttpResponse<String> response = client.send(httpRequestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return getResponseEntity(response, responseType);
    }

    private <T> ResponseEntity<T> getResponseEntity(HttpResponse<String> response, Class<T> responseType) throws JsonProcessingException {
        return new ResponseEntity<>(objectMapper.readValue(response.body(), responseType), response.statusCode());
    }
}
