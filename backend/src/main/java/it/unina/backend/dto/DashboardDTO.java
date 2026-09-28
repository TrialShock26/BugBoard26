package it.unina.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private Integer totalBugs;
    private Map<UserDTO, Integer> bugsPerUser = new HashMap<>();
    private Double averageGlobalResolutionTime;
    private Map<UserDTO, Double> averageResolutionTimePerUser = new HashMap<>();
}