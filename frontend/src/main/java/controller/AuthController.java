package controller;

import config.ApiPaths;
import dto.UserDTO;
import dto.UserRole;
import json.Json;

import java.net.http.HttpRequest;
import java.util.Map;

import static controller.JsonMapping.*;

public final class AuthController {

    private AuthController() { }

    @SuppressWarnings("unchecked")
    public static UserDTO login(String email, String password) {
        HttpRequest req = ApiClient.request(ApiPaths.LOGIN)
                .POST(ApiClient.json(Json.obj("email", email, "password", password))).build();
        Map<String, Object> m = (Map<String, Object>) ApiClient.call(req);
        return toUserDTO(m);
    }

    private static UserDTO toUserDTO(Map<String, Object> m) {
        Map<String, Object> user = mapOrNull(m, "user");
        return new UserDTO(
                user == null ? null : str(user, "userId"),
                str(m, "token"),
                user == null ? null : str(user, "email"),
                user == null ? null : enumVal(user, "type", UserRole.class)
        );
    }
}
