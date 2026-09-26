package controller;

import config.ApiPaths;
import dto.LoginResponseDTO;

import java.net.http.HttpRequest;
import java.util.Map;

public final class AuthController {

    private AuthController() { }

    public static LoginResponseDTO login(String email, String password) {
        HttpRequest req = ApiClient.request(ApiPaths.LOGIN)
                .POST(ApiClient.json(Map.of("email", email, "password", password))).build();
        return ApiClient.call(req, LoginResponseDTO.class);
    }
}
