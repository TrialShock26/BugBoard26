package controller;

import com.fasterxml.jackson.core.type.TypeReference;
import config.ApiPaths;
import dto.*;
import exception.ApiException;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IssueController {

    private IssueController() { }

    public static List<IssueDTO> listIssues(IssueType type, IssueStatus status, IssuePriority priority, String sort) {

        StringBuilder q = new StringBuilder(ApiPaths.ISSUES + "?");
        q.append("assignee=").append("all").append("&");
        if (type != null) q.append("type=").append(type).append("&");
        if (status != null) q.append("status=").append(status).append("&");
        if (priority != null) q.append("priority=").append(priority).append("&");
        if (sort != null) q.append("sort=").append(sort).append("&");
        HttpRequest req = ApiClient.request(q.toString()).GET().build();
        return ApiClient.callList(req, new TypeReference<List<IssueDTO>>() { });
    }


    public static List<IssueDTO> myAssignedIssues() {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES).GET().build();
        return ApiClient.callList(req, new TypeReference<List<IssueDTO>>() { });
    }
    public static IssueDTO createIssue(String title, String description,
                                       IssueType type, IssuePriority priority,
                                       String projectId, List<String> tags,
                                       String imageBase64) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("description", description);
        body.put("type", type);
        body.put("priority", priority);
        body.put("project", Integer.valueOf(projectId));
        body.put("tags", tags);
        body.put("image", imageBase64);

        HttpRequest req = ApiClient.request(ApiPaths.ISSUES)
                .POST(ApiClient.json(body))
                .build();

        return ApiClient.call(req, IssueDTO.class);
    }

    public static IssueDTO handleIssue(String issueId) {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES + "/" + issueId + "/handle")
                .method("PUT", BodyPublishers.noBody()).build();
        return ApiClient.call(req, IssueDTO.class);
    }

    public static IssueDTO resolveIssue(String issueId) {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES + "/" + issueId + "/resolve")
                .method("PUT", BodyPublishers.noBody()).build();
        return ApiClient.call(req, IssueDTO.class);
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


    public static boolean mySuggestions() {
        HttpRequest req = ApiClient.request(ApiPaths.ISSUES + "/suggestion").GET().build();
        return ApiClient.call(req, Boolean.class);
    }

}


