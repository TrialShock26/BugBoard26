package controller;

import config.ApiPaths;
import dto.TeamDTO;
import json.Json;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static controller.JsonMapping.*;

public final class TeamController {

    private TeamController() { }

    @SuppressWarnings("unchecked")
    public static List<TeamDTO> listTeams() {
        HttpRequest req = ApiClient.request(ApiPaths.TEAMS).GET().build();
        List<Map<String, Object>> raw = (List<Map<String, Object>>) ApiClient.call(req);
        List<TeamDTO> out = new ArrayList<>();
        for (Map<String, Object> m : raw) out.add(toTeamDTO(m));
        return out;
    }


    @SuppressWarnings("unchecked")
    public static List<TeamDTO> createTeams(String project, List<String> teamNames) {
        HttpRequest req = ApiClient.request(ApiPaths.TEAMS)
                .POST(ApiClient.json(Json.obj("project", project, "teamNames", teamNames))).build();
        List<Map<String, Object>> raw = (List<Map<String, Object>>) ApiClient.call(req);
        List<TeamDTO> out = new ArrayList<>();
        for (Map<String, Object> m : raw) out.add(toTeamDTO(m));
        return out;
    }

    @SuppressWarnings("unchecked")
    public static List<TeamDTO> myTeams() {
        HttpRequest req = ApiClient.request(ApiPaths.MY_TEAM).GET().build();
        List<Map<String, Object>> raw = (List<Map<String, Object>>) ApiClient.call(req);
        List<TeamDTO> out = new ArrayList<>();
        for (Map<String, Object> m : raw) out.add(toTeamDTO(m));
        return out;
    }

    public static void joinTeam(String teamId) {
        HttpRequest req = ApiClient.request(ApiPaths.MY_TEAM)
                .method("PUT", ApiClient.json(Json.obj("teamId", teamId))).build();
        ApiClient.call(req);
    }

    public static void leaveTeam(String teamId) {
        HttpRequest req = ApiClient.request(ApiPaths.MY_TEAM + "?teamId=" + teamId)
                .method("DELETE", BodyPublishers.noBody()).build();
        ApiClient.call(req);
    }

    private static TeamDTO toTeamDTO(Map<String, Object> m) {
        return new TeamDTO(str(m, "id"), str(m, "name"), str(m, "project"));
    }
}
