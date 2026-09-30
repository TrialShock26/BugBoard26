package dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class StatisticDTO {
    private Integer openBugs;
    private Integer ongoingBugs;
    private Integer doneBugs;
    private Integer totalBugs;
    private Double averageGlobalResolutionTime;
    private Map<UserDTO, Integer> bugsPerUser = new HashMap<>();
    private Map<UserDTO, Double> averageResolutionTimePerUser = new HashMap<>();

    public void setBugsPerUser(Map<UserDTO, Integer> bugsPerUser) {
        this.bugsPerUser = bugsPerUser == null ? new HashMap<>() : bugsPerUser;
    }

    public void setAverageResolutionTimePerUser(Map<UserDTO, Double> averageResolutionTimePerUser) {
        this.averageResolutionTimePerUser = averageResolutionTimePerUser == null
                ? new HashMap<>() : averageResolutionTimePerUser;
    }
}
