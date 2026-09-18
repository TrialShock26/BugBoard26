package dto;

import java.util.ArrayList;
import java.util.List;


public class ReportDTO {

    private int opened;
    private int resolved;
    private Double avgResolutionHours;
    private List<UserReportStat> perUser = new ArrayList<>();
    private List<TeamReportStat> perTeam = new ArrayList<>();

    public int getOpened() { return opened; }
    public void setOpened(int opened) { this.opened = opened; }

    public int getResolved() { return resolved; }
    public void setResolved(int resolved) { this.resolved = resolved; }

    public Double getAvgResolutionHours() { return avgResolutionHours; }
    public void setAvgResolutionHours(Double avgResolutionHours) { this.avgResolutionHours = avgResolutionHours; }

    public List<UserReportStat> getPerUser() { return perUser; }
    public void setPerUser(List<UserReportStat> perUser) { this.perUser = perUser != null ? perUser : new ArrayList<>(); }

    public List<TeamReportStat> getPerTeam() { return perTeam; }
    public void setPerTeam(List<TeamReportStat> perTeam) { this.perTeam = perTeam != null ? perTeam : new ArrayList<>(); }

    /** Numero di issue aperte/risolte nel mese da un singolo utente. */
    public static class UserReportStat {
        private String email;
        private int opened;
        private int resolved;
        private Double avgResolutionHours;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public int getOpened() { return opened; }
        public void setOpened(int opened) { this.opened = opened; }

        public int getResolved() { return resolved; }
        public void setResolved(int resolved) { this.resolved = resolved; }

        public Double getAvgResolutionHours() { return avgResolutionHours; }
        public void setAvgResolutionHours(Double avgResolutionHours) { this.avgResolutionHours = avgResolutionHours; }
    }

    /** Numero di issue aperte/risolte nel mese da un singolo team. */
    public static class TeamReportStat {
        private String team;
        private int opened;
        private int resolved;
        private Double avgResolutionHours;

        public String getTeam() { return team; }
        public void setTeam(String team) { this.team = team; }

        public int getOpened() { return opened; }
        public void setOpened(int opened) { this.opened = opened; }

        public int getResolved() { return resolved; }
        public void setResolved(int resolved) { this.resolved = resolved; }

        public Double getAvgResolutionHours() { return avgResolutionHours; }
        public void setAvgResolutionHours(Double avgResolutionHours) { this.avgResolutionHours = avgResolutionHours; }
    }
}
