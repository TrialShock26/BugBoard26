package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import config.ApiPaths;
import dto.UserDTO;
import dto.UserRole;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

public final class UserController {

    private UserController() { }


    public static List<UserDTO> listUsers() {
        HttpRequest req = ApiClient.request(ApiPaths.USERS).GET().build();
        return ApiClient.call(req, new TypeReference<List<UserDTO>>() { });
    }

    public static UserDTO createUser(String email, String password, UserRole role) {
        HttpRequest req = ApiClient.request(ApiPaths.USERS)
                .POST(ApiClient.json(Map.of("email", email, "password", password, "role", role)))
                .build();
        return ApiClient.call(req, UserDTO.class);
    }
}
