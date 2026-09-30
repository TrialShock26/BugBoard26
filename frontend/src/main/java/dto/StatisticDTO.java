package dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class StatisticDTO {
    private Integer openBugs;
    private Integer ongoingBugs;
    private Integer doneBugs;
    private Integer totalBugs;
    private Double averageGlobalResolutionTime;
    private List<UserBugsDTO> bugsPerUser = new ArrayList<>();
    private List<UserTimeDTO> averageResolutionTimePerUser = new ArrayList<>();

    public void setBugsPerUser(List<UserBugsDTO> bugsPerUser) {
        this.bugsPerUser = bugsPerUser == null ? new ArrayList<>() : bugsPerUser;
    }

    public void setAverageResolutionTimePerUser(List<UserTimeDTO> averageResolutionTimePerUser) {
        this.averageResolutionTimePerUser = averageResolutionTimePerUser == null
                ? new ArrayList<>() : averageResolutionTimePerUser;
    }
}
