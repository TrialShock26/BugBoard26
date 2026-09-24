package frontend.controller;

import frontend.config.ApiPaths;
import frontend.dto.ProjectDTO;
import frontend.dto.TeamDTO;
import frontend.json.Json;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static frontend.controller.JsonMapping.*;

public final class ProjectController {

    private ProjectController() { }

    @SuppressWarnings("unchecked")
    public static List<ProjectDTO> listProjects() {
        // GET /projects
        HttpRequest req = ApiClient.request(ApiPaths.PROJECTS).GET().build();
        List<Map<String, Object>> raw = (List<Map<String, Object>>) ApiClient.call(req);
        List<ProjectDTO> out = new ArrayList<>();
        for (Map<String, Object> m : raw) out.add(new ProjectDTO(str(m, "id"), str(m, "name")));
        return out;
    }

    public static void createProject(String name, List<String> teamNames) {
        // POST /projects: crea sia il nuovo progetto sia i team indicati.
        HttpRequest req = ApiClient.request(ApiPaths.PROJECTS)
                .POST(ApiClient.json(Json.obj("name", name, "teamNames", teamNames))).build();
        ApiClient.call(req);
    }

    @SuppressWarnings("unchecked")
    public static List<TeamDTO> listTeams(String projectId) {
        // GET /projects/{id}/teams
        HttpRequest req = ApiClient.request(ApiPaths.PROJECTS + "/" + projectId + "/teams").GET().build();
        List<Map<String, Object>> raw = (List<Map<String, Object>>) ApiClient.call(req);
        List<TeamDTO> out = new ArrayList<>();
        for (Map<String, Object> m : raw) out.add(new TeamDTO(str(m, "id"), str(m, "name"), str(m, "project")));
        return out;
    }

    public static void joinTeam(String projectId, String teamId) {
        // PUT /projects/{id}/teams/{id}: l'utente si unisce al team indicato,
        // all'interno del progetto indicato.
        HttpRequest req = ApiClient.request(ApiPaths.PROJECTS + "/" + projectId + "/teams/" + teamId)
                .method("PUT", BodyPublishers.noBody()).build();
        ApiClient.call(req);
    }
}
