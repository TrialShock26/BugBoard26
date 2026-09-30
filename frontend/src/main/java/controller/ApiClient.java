package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ErrorResponseDTO;
import exception.ApiException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public final class ApiClient {

    private static final String BASE_URL = System.getProperty("bugboard.api", "http://localhost:8080");
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private ApiClient() { }

    static HttpRequest.Builder request(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder().uri(URI.create(BASE_URL + path));
        if (Session.getToken() != null) b.header("Authorization", "Bearer " + Session.getToken());
        b.header("Content-Type", "application/json; charset=utf-8");
        return b;
    }

    static HttpRequest.BodyPublisher json(Object body) {
        try {
            return HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(body), StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not serialize request body", e);
        }
    }

    static <T> T call(HttpRequest req, Class<T> type) {
        String body = send(req);
        try {
            return MAPPER.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new ApiException(0, "Invalid server answer.");
        }
    }

    static <T> T call(HttpRequest req, TypeReference<T> type) {
        String body = send(req);
        try {
            return MAPPER.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new ApiException(0, "Invalid server answer.");
        }
    }

    static <T> List<T> callList(HttpRequest req, TypeReference<List<T>> type) {
        List<T> result = call(req, type);
        return result == null ? Collections.emptyList() : result;
    }

    static void call(HttpRequest req) {
        send(req);
    }

    private static String send(HttpRequest req) {
        try {
            HttpResponse<String> resp = CLIENT.send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = resp.body();
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return (body == null || body.isEmpty()) ? "null" : body;
            }
            throw new ApiException(resp.statusCode(), errorMessage(resp.statusCode(), body));
        } catch (IOException | InterruptedException e) {
            throw new ApiException(0, "Could not reach the BugBoard26 server (" + BASE_URL + "). "
                    + "Please make sure the back-end is running.");
        }
    }

    private static String errorMessage(int status, String body) {
        String msg;
        switch (status) {
            case 400: msg = "The request contains invalid or incomplete information. Review the fields and try again."; break;
            case 401: msg = "Your session has expired or you are not signed in. Sign in again and retry."; break;
            case 403: msg = "You do not have permission to perform this action."; break;
            case 404: msg = "The requested item could not be found. It may have been removed or the address may be incorrect."; break;
            case 409: msg = "This action conflicts with existing data. Check for a duplicate or an item that has changed."; break;
            case 422: msg = "The server could not process the supplied information. Review the fields and try again."; break;
            case 500: case 502: case 503: case 504:
                msg = "The server encountered a problem while processing the request. Please try again later."; break;
            default: msg = "The request failed (HTTP " + status + "). Please try again.";
        }
        if (body != null && !body.isEmpty()) {
            try {
                ErrorResponseDTO err = MAPPER.readValue(body, ErrorResponseDTO.class);

                if (err.getMessage() != null && !err.getMessage().isBlank()) msg += "\nDetails: " + err.getMessage();
                else if (err.getError() != null && !err.getError().isBlank()) msg += "\nDetails: " + err.getError();
            } catch (JsonProcessingException ignored) {
            }
        }
        return msg;
    }

    static byte[] callBytes(HttpRequest req) {
        try {
            HttpResponse<byte[]> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) return resp.body();
            throw new ApiException(resp.statusCode(), errorMessage(resp.statusCode(), ""));
        } catch (IOException | InterruptedException e) {
            throw new ApiException(0, "Could not reach the BugBoard26 server (" + BASE_URL + "). "
                    + "Please make sure the back-end is running.");
        }
    }
}