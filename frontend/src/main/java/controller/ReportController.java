package controller;

import config.ApiPaths;
import dto.ReportDTO;
import dto.ProjectDTO;
import dto.StatisticDTO;

import java.net.http.HttpRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class ReportController {

    private ReportController() { }

    public static StatisticDTO dashboard() {
        HttpRequest req = ApiClient.request(ApiPaths.DASHBOARD).GET().build();
        return ApiClient.call(req, StatisticDTO.class);
    }

    public static List<ProjectDTO> projectsForAdmin() {
        return ProjectController.listMyProjects();
    }

    public static ReportDTO monthlyReport(int year, int month, String projectName) {
        String project = URLEncoder.encode(projectName, StandardCharsets.UTF_8);
        HttpRequest req = ApiClient.request(ApiPaths.REPORTS + "?month=" + month + "&year=" + year + "&project=" + project)
                .GET().build();
        return ApiClient.call(req, ReportDTO.class);
    }
}

