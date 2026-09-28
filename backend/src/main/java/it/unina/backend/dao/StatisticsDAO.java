package it.unina.backend.dao;

import it.unina.backend.dto.*;

public interface StatisticsDAO {
    DashboardDTO getDashboardData(String email);
    ReportDTO getReportData(Integer month, Integer year, String project);
}