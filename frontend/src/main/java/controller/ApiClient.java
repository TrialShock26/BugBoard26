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
            throw new ApiException(0, "Risposta del server non valida.");
        }
    }

    static <T> T call(HttpRequest req, TypeReference<T> type) {
        String body = send(req);
        try {
            return MAPPER.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new ApiException(0, "Risposta del server non valida.");
        }
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
        String msg = "Errore del server (HTTP " + status + ")";
        if (body != null && !body.isEmpty()) {
            try {
                ErrorResponseDTO err = MAPPER.readValue(body, ErrorResponseDTO.class);
                // Il GlobalExceptionHandler del backend reale risponde con {"message","timestamp"};
                // gli errori generici di Spring Boot (401, validazione @Valid) usano invece {"error",...}.
                if (err.getMessage() != null) msg = err.getMessage();
                else if (err.getError() != null) msg = err.getError();
            } catch (JsonProcessingException ignored) {
                // corpo non-JSON (es. pagina d'errore HTML): teniamo il messaggio generico sopra
            }
        }
        return msg;
    }

    static byte[] callBytes(HttpRequest req) {
        try {
            HttpResponse<byte[]> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) return resp.body();
            throw new ApiException(resp.statusCode(), "Errore del server (HTTP " + resp.statusCode() + ")");
        } catch (IOException | InterruptedException e) {
            throw new ApiException(0, "Could not reach the BugBoard26 server (" + BASE_URL + "). "
                    + "Please make sure the back-end is running.");
        }
    }
}
