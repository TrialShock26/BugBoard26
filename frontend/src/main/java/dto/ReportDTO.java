package dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
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

    public void setTotalBugsPerTeam(Map<TeamDTO, Integer> value) {
        totalBugsPerTeam = value == null ? new HashMap<>() : value;
    }

    public void setTotalHandledBugsPerTeam(Map<TeamDTO, Integer> value) {
        totalHandledBugsPerTeam = value == null ? new HashMap<>() : value;
    }

    public void setAverageResolutionTimePerTeam(Map<TeamDTO, Double> value) {
        averageResolutionTimePerTeam = value == null ? new HashMap<>() : value;
    }

    public void setTotalBugsPerUser(Map<UserDTO, Integer> value) {
        totalBugsPerUser = value == null ? new HashMap<>() : value;
    }

    public void setTotalHandledBugsPerUser(Map<UserDTO, Integer> value) {
        totalHandledBugsPerUser = value == null ? new HashMap<>() : value;
    }

    public void setAverageResolutionTimePerUser(Map<UserDTO, Double> value) {
        averageResolutionTimePerUser = value == null ? new HashMap<>() : value;
    }
}
