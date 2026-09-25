package controller;

import config.ApiPaths;
import dto.UserDTO;
import dto.UserRole;
import json.Json;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static controller.JsonMapping.*;

public final class UserController {

    private UserController() { }

    @SuppressWarnings("unchecked")
    public static List<UserDTO> listUsers() {
        HttpRequest req = ApiClient.request(ApiPaths.USERS).GET().build();
        List<Map<String, Object>> raw = (List<Map<String, Object>>) ApiClient.call(req);
        List<UserDTO> out = new ArrayList<>();
        for (Map<String, Object> m : raw) out.add(toUserDTO(m));
        return out;
    }

    @SuppressWarnings("unchecked")
    public static UserDTO createUser(String email, String password, UserRole role) {
        HttpRequest req = ApiClient.request(ApiPaths.USERS)
                .POST(ApiClient.json(Json.obj("email", email, "password", password, "role", role)))
                .build();
        Map<String, Object> m = (Map<String, Object>) ApiClient.call(req);
        return toUserDTO(m);
    }

    private static UserDTO toUserDTO(Map<String, Object> m) {
        return new UserDTO(
                str(m, "userId"),
                null,
                str(m, "email"),
                enumVal(m, "type", UserRole.class)
        );
    }
}
