package it.unina.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Integer totalBugs;
    private Integer totalHandledBugs;
    private Double averageGlobalResolutionTime;
    private Map<TeamDTO, Integer> totalBugsPerTeam = new HashMap<>();
    private Map<TeamDTO, Integer> totalHandledBugsPerTeam = new HashMap<>();
    private Map<TeamDTO, Double> averageResolutionTimePerTeam = new HashMap<>();
    private Map<UserDTO, Integer> totalBugsPerUser = new HashMap<>();
    private Map<UserDTO, Integer> totalHandledBugsPerUser = new HashMap<>();
    private Map<UserDTO, Double> averageResolutionTimePerUser = new HashMap<>();
}