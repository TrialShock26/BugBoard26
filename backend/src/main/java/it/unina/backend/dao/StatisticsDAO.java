package it.unina.backend.dao;

import it.unina.backend.dto.*;

public interface StatisticsDAO {
    DashboardDTO getDashboardData();
    ReportDTO getReportData(String month, String year);
}