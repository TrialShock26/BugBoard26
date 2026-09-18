package controller;

import config.ApiPaths;
import dto.IssueDTO;
import dto.IssuePriority;
import dto.IssueStatus;
import dto.IssueType;
import dto.SuggestionsDTO;
import exception.ApiException;
import json.Json;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static controller.JsonMapping.*;

public final class IssueController {

    private IssueController() { }

    @SuppressWarnings("unchecked")
    public static List<IssueDTO> listIssues(IssueType type, IssueStatus status, IssuePriority priority, String sort) {
        StringBuilder q = new StringBuilder(ApiPaths.ISSUES + "?");
        if (type != null) q.append("type=").append(type).append("&");
        if (status != null) q.append("status=").append(status).append("&");
        if (priority != null) q.append("priority=").append(priority).append("&");
        if (sort != null) q.append("sort=").append(sort).append("&");
        HttpRequest req = ApiClient.request(q.toString()).GET().build();
        List<Map<String, Object>> raw = (List<Map<String, Object>>) ApiClient.call(req);
        List<IssueDTO> out = new ArrayList<>();
        for (Map<String, Object> m : raw) out.add(toIssueDTO(m));
        return out;
    }

    @SuppressWarnings("unchecked")
    public static IssueDTO createIssue(String title, String description, IssueType type, IssuePriority priority,
                                        List<String> labels, String imageBase64) {
        Map<String, Object> body = Json.obj(
                "title", title, "description", description, "type", type, "priority", priority,
                "labels", labels, "imageBase64", imageBase64);
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES).POST(ApiClient.json(body)).build();
        Map<String, Object> m = (Map<String, Object>) ApiClient.call(req);
        return toIssueDTO(m);
    }

    @SuppressWarnings("unchecked")
    public static IssueDTO handleIssue(String issueId) {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES + "/" + issueId + "/handle")
                .method("PUT", BodyPublishers.noBody()).build();
        Map<String, Object> m = (Map<String, Object>) ApiClient.call(req);
        return toIssueDTO(m);
    }

    @SuppressWarnings("unchecked")
    public static IssueDTO resolveIssue(String issueId) {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES + "/" + issueId + "/resolve")
                .method("PUT", BodyPublishers.noBody()).build();
        Map<String, Object> m = (Map<String, Object>) ApiClient.call(req);
        return toIssueDTO(m);
    }

    public static byte[] getIssueImage(String issueId) {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES + "/" + issueId + "/image").GET().build();
        try {
            return ApiClient.callBytes(req);
        } catch (ApiException ex) {
            if (ex.status == 404) return null;
            throw ex;
        }
    }

    public static void deleteIssue(String issueId) {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES + "/" + issueId)
                .method("DELETE", BodyPublishers.noBody()).build();
        ApiClient.call(req);
    }
    @SuppressWarnings("unchecked")
    public static SuggestionsDTO mySuggestions() {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES + "/suggestions").GET().build();
        Map<String, Object> m = (Map<String, Object>) ApiClient.call(req);

        List<IssueDTO> available = new ArrayList<>();
        for (Map<String, Object> issue : mapList(m, "availableIssues")) available.add(toIssueDTO(issue));

        return new SuggestionsDTO(boolVal(m, "eligible"), intOrNull(m, "myWorkload"), available);
    }

    @SuppressWarnings("unchecked")
    public static IssueDTO addLabel(String issueId, String label) {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES + "/" + issueId + "/labels")
                .POST(ApiClient.json(Json.obj("label", label))).build();
        Map<String, Object> m = (Map<String, Object>) ApiClient.call(req);
        return toIssueDTO(m);
    }

    private static IssueDTO toIssueDTO(Map<String, Object> m) {
        return new IssueDTO(
                str(m, "id"),
                str(m, "title"),
                str(m, "description"),
                enumVal(m, "type", IssueType.class),
                enumVal(m, "priority", IssuePriority.class),
                enumVal(m, "status", IssueStatus.class),
                str(m, "assigneeEmail"),
                stringList(m, "labels")
        );
    }
}
