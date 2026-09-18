package controller;

import exception.ApiException;
import json.Json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiClient {

    private static final String BASE_URL = System.getProperty("bugboard.api", "http://localhost:8080");
    private static final HttpClient client = HttpClient.newHttpClient();

    private ApiClient() { }

    static HttpRequest.Builder request(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder().uri(URI.create(BASE_URL + path));
        if (Session.getToken() != null) b.header("Authorization", "Bearer " + Session.getToken());
        b.header("Content-Type", "application/json; charset=utf-8");
        return b;
    }

    static BodyPublisher json(Map<String, Object> body) {
        return BodyPublishers.ofString(Json.write(body), StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    static Object call(HttpRequest req) {
        try {
            HttpResponse<String> resp = client.send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
            String bodyStr = (resp.body() == null || resp.body().isEmpty()) ? "null" : resp.body();
            Object parsed = Json.parse(bodyStr);
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) return parsed;
            String msg = "Errore del server (HTTP " + resp.statusCode() + ")";
            if (parsed instanceof Map && ((Map<String, Object>) parsed).get("error") != null) {
                msg = String.valueOf(((Map<String, Object>) parsed).get("error"));
            }
            throw new ApiException(resp.statusCode(), msg);
        } catch (IOException | InterruptedException e) {
            throw new ApiException(0, "Could not reach the BugBoard26 server (" + BASE_URL + "). "
                    + "Please make sure the back-end is running.");
        }
    }

    static byte[] callBytes(HttpRequest req) {
        try {
            HttpResponse<byte[]> resp = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) return resp.body();
            throw new ApiException(resp.statusCode(), "Errore del server (HTTP " + resp.statusCode() + ")");
        } catch (IOException | InterruptedException e) {
            throw new ApiException(0, "Could not reach the BugBoard26 server (" + BASE_URL + "). "
                    + "Please make sure the back-end is running.");
        }
    }
}
