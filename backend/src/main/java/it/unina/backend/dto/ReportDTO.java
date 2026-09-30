package it.unina.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Integer totalBugs;
    private Integer totalHandledBugs;
    private Double averageGlobalResolutionTime;
    private List<TeamBugsDTO> totalBugsPerTeam = new ArrayList<>();
    private List<TeamBugsDTO> totalHandledBugsPerTeam = new ArrayList<>();
    private List<TeamTimeDTO> averageResolutionTimePerTeam = new ArrayList<>();
    private List<UserBugsDTO> totalBugsPerUser = new ArrayList<>();
    private List<UserBugsDTO> totalHandledBugsPerUser = new ArrayList<>();
    private List<UserTimeDTO> averageResolutionTimePerUser = new ArrayList<>();
}