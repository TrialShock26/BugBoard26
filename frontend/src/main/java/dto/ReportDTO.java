package dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
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