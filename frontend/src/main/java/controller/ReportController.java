package controller;

import config.ApiPaths;
import dto.ReportDTO;
import dto.StatisticDTO;

import java.net.http.HttpRequest;

public final class ReportController {

    private ReportController() { }

    public static StatisticDTO dashboard() {
        HttpRequest req = ApiClient.request(ApiPaths.DASHBOARD).GET().build();
        return ApiClient.call(req, StatisticDTO.class);
    }

    public static ReportDTO monthlyReport(int year, int month) {
        HttpRequest req = ApiClient.request(ApiPaths.REPORTS + "?month=" + month + "&year=" + year)
                .GET().build();
        return ApiClient.call(req, ReportDTO.class);
    }
}
