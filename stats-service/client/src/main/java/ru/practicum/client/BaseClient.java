package ru.practicum.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, headers, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> parameters,
            T body) {
        return makeAndSendRequest(HttpMethod.POST, path, headers, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> parameters,
            T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, headers, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, headers, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> headers,
            @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, getHeaders(headers));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders getHeaders(Map<String, Object> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (headers != null) {
        	for (String key:headers.keySet()) {
            	httpHeaders.add(key, String.valueOf(headers.get(key)));
            }
        }
        return httpHeaders;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}