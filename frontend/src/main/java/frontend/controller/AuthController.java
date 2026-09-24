package frontend.controller;

import frontend.config.ApiPaths;
import frontend.dto.UserDTO;
import frontend.dto.UserRole;
import frontend.json.Json;

import java.net.http.HttpRequest;
import java.util.Map;

import static frontend.controller.JsonMapping.*;

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
        return new UserDTO(
                str(m, "id"),
                str(m, "token"),
                str(m, "email"),
                str(m, "name"),
                enumVal(m, "role", UserRole.class),
                str(m, "team")
        );
    }
}
