package it.unina.backend.controller;

import it.unina.backend.dao.StatisticsDAO;
import it.unina.backend.dto.DashboardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {
    private StatisticsDAO dao;

    public StatisticsController(StatisticsDAO dao) {
        this.dao = dao;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDTO> getDashboardData() {
        return ResponseEntity.ok(dao.getDashboardData());
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> getReportsData(@RequestParam String month,
                                                    @RequestParam String year) {
        return ResponseEntity.ok(dao.getReportData(month, year));
    }
}