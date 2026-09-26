package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import config.ApiPaths;
import dto.TeamDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.List;

public final class TeamController {

    private TeamController() { }

    public static List<TeamDTO> myTeams() {
        HttpRequest req = ApiClient.request(ApiPaths.MY_TEAM).GET().build();
        return ApiClient.call(req, new TypeReference<List<TeamDTO>>() { });
    }

    public static void leaveTeam(String teamId) {
        HttpRequest req = ApiClient.request(ApiPaths.MY_TEAM + "?teamId=" + teamId)
                .method("DELETE", BodyPublishers.noBody()).build();
        ApiClient.call(req);
    }
}
