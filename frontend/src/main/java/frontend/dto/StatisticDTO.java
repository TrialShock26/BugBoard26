package frontend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StatisticDTO {

    @Setter private int openCount;
    @Setter private int ongoingCount;
    @Setter private int resolvedCount;
    @Setter private int totalCount;
    @Setter private Double avgResolutionHoursOverall;
    private List<UserStat> perUser = new ArrayList<>();
    private List<TeamStat> perTeam = new ArrayList<>();

    public void setPerUser(List<UserStat> perUser) {
        this.perUser = perUser != null ? perUser : new ArrayList<>();
    }

    public void setPerTeam(List<TeamStat> perTeam) {
        this.perTeam = perTeam != null ? perTeam : new ArrayList<>();
    }

    /** Carico di lavoro di un singolo utente, come mostrato nella dashboard. */
    @Data
    public static class UserStat {
        private String name;
        private String email;
        private int assignedCount;
        private int openCount;
        private Double avgResolutionHours;
    }

    /** Carico di lavoro di un singolo team, come mostrato nella dashboard. */
    @Data
    public static class TeamStat {
        private String team;
        private int memberCount;
        private int assignedCount;
        private int openCount;
        private Double avgResolutionHours;
    }
}
