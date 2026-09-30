package it.unina.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private Integer openBugs;
    private Integer ongoingBugs;
    private Integer doneBugs;
    private Integer totalBugs;
    private Double averageGlobalResolutionTime;
    private List<UserBugsDTO> bugsPerUser = new ArrayList<>();
    private List<UserTimeDTO> averageResolutionTimePerUser = new ArrayList<>();
}