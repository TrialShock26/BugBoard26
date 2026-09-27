package it.unina.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private Integer totalBugs;
    private Map<UserDTO, Integer> bugsPerUser;
    private Double averageGlobalResolutionTime;
    private Map<UserDTO, Double> averageResolutionTimePerUser;
}