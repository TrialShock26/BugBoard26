package dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ReportDTO {

    @Setter private int opened;
    @Setter private int resolved;
    @Setter private Double avgResolutionHours;
    private List<UserReportStat> perUser = new ArrayList<>();
    private List<TeamReportStat> perTeam = new ArrayList<>();

    public void setPerUser(List<UserReportStat> perUser) {
        this.perUser = perUser != null ? perUser : new ArrayList<>();
    }

    public void setPerTeam(List<TeamReportStat> perTeam) {
        this.perTeam = perTeam != null ? perTeam : new ArrayList<>();
    }

    /** Numero di issue aperte/risolte nel mese da un singolo utente. */
    @Data
    public static class UserReportStat {
        private String email;
        private int opened;
        private int resolved;
        private Double avgResolutionHours;
    }

    /** Numero di issue aperte/risolte nel mese da un singolo team. */
    @Data
    public static class TeamReportStat {
        private String team;
        private int opened;
        private int resolved;
        private Double avgResolutionHours;
    }
}
