package controller;

import config.ApiPaths;
import dto.ReportDTO;
import dto.StatisticDTO;

import java.net.http.HttpRequest;
import java.util.Map;

import static controller.JsonMapping.*;

public final class ReportController {

    private ReportController() { }

    @SuppressWarnings("unchecked")
    public static StatisticDTO dashboard() {
        HttpRequest req = ApiClient.request(ApiPaths.DASHBOARD).GET().build();
        Map<String, Object> m = (Map<String, Object>) ApiClient.call(req);

        StatisticDTO stats = new StatisticDTO();
        stats.setOpenCount(intVal(m, "openCount"));
        stats.setOngoingCount(intVal(m, "ongoingCount"));
        stats.setResolvedCount(intVal(m, "resolvedCount"));
        stats.setTotalCount(intVal(m, "totalCount"));
        stats.setAvgResolutionHoursOverall(doubleOrNull(m, "avgResolutionHoursOverall"));

        for (Map<String, Object> row : mapList(m, "perUser")) {
            StatisticDTO.UserStat u = new StatisticDTO.UserStat();
            u.setName(str(row, "name"));
            u.setEmail(str(row, "email"));
            u.setAssignedCount(intVal(row, "assignedCount"));
            u.setOpenCount(intVal(row, "openCount"));
            u.setAvgResolutionHours(doubleOrNull(row, "avgResolutionHours"));
            stats.getPerUser().add(u);
        }

        for (Map<String, Object> row : mapList(m, "perTeam")) {
            StatisticDTO.TeamStat t = new StatisticDTO.TeamStat();
            t.setTeam(str(row, "team"));
            t.setMemberCount(intVal(row, "memberCount"));
            t.setAssignedCount(intVal(row, "assignedCount"));
            t.setOpenCount(intVal(row, "openCount"));
            t.setAvgResolutionHours(doubleOrNull(row, "avgResolutionHours"));
            stats.getPerTeam().add(t);
        }

        return stats;
    }

    @SuppressWarnings("unchecked")
    public static ReportDTO monthlyReport(int year, int month) {
        HttpRequest req = ApiClient.request(ApiPaths.REPORTS + "?month=" + month + "&year=" + year)
                .GET().build();
        Map<String, Object> m = (Map<String, Object>) ApiClient.call(req);

        ReportDTO report = new ReportDTO();
        report.setOpened(intVal(m, "opened"));
        report.setResolved(intVal(m, "resolved"));
        report.setAvgResolutionHours(doubleOrNull(m, "avgResolutionHours"));

        for (Map<String, Object> row : mapList(m, "perUser")) {
            ReportDTO.UserReportStat u = new ReportDTO.UserReportStat();
            u.setEmail(str(row, "email"));
            u.setOpened(intVal(row, "opened"));
            u.setResolved(intVal(row, "resolved"));
            u.setAvgResolutionHours(doubleOrNull(row, "avgResolutionHours"));
            report.getPerUser().add(u);
        }

        for (Map<String, Object> row : mapList(m, "perTeam")) {
            ReportDTO.TeamReportStat t = new ReportDTO.TeamReportStat();
            t.setTeam(str(row, "team"));
            t.setOpened(intVal(row, "opened"));
            t.setResolved(intVal(row, "resolved"));
            t.setAvgResolutionHours(doubleOrNull(row, "avgResolutionHours"));
            report.getPerTeam().add(t);
        }

        return report;
    }
}
