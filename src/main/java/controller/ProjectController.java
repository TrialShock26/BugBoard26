package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import config.ApiPaths;
import dto.ProjectDTO;
import dto.TeamDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.List;
import java.util.Map;

public final class ProjectController {

    private ProjectController() { }

    public static List<ProjectDTO> listProjects() {
        HttpRequest req = ApiClient.request(ApiPaths.PROJECTS).GET().build();
        return ApiClient.call(req, new TypeReference<List<ProjectDTO>>() { });
    }

    public static void createProject(String name, List<String> teamNames) {
        HttpRequest req = ApiClient.request(ApiPaths.PROJECTS)
                .POST(ApiClient.json(Map.of("name", name, "teamNames", teamNames))).build();
        ApiClient.call(req);
    }

    public static List<TeamDTO> listTeams(String projectId) {
        HttpRequest req = ApiClient.request(ApiPaths.PROJECTS + "/" + projectId + "/teams").GET().build();
        return ApiClient.call(req, new TypeReference<List<TeamDTO>>() { });
    }

    public static void joinTeam(String projectId, String teamId) {
        HttpRequest req = ApiClient.request(ApiPaths.PROJECTS + "/" + projectId + "/teams/" + teamId)
                .method("PUT", BodyPublishers.noBody()).build();
        ApiClient.call(req);
    }
}
