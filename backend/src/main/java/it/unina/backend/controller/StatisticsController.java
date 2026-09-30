package it.unina.backend.controller;

import it.unina.backend.dao.StatisticsDAO;
import it.unina.backend.dto.DashboardDTO;
import it.unina.backend.dto.ReportDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.Objects;

@RestController
public class StatisticsController {
    private StatisticsDAO dao;

    public StatisticsController(StatisticsDAO dao) {
        this.dao = dao;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDTO> getDashboardData() {
        return ResponseEntity.ok(dao.getDashboardData(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName()));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> getReportsData(@RequestParam Integer month,
                                                    @RequestParam Integer year,
                                                    @RequestParam String project) {
        if (month < 1 || month > 12 || year < 2020 || year > Year.now().getValue()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(dao.getReportData(month, year, project));
    }
}