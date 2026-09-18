package dto;

import java.util.ArrayList;
import java.util.List;


public class StatisticDTO {

    private int openCount;
    private int ongoingCount;
    private int resolvedCount;
    private int totalCount;
    private Double avgResolutionHoursOverall;
    private List<UserStat> perUser = new ArrayList<>();
    private List<TeamStat> perTeam = new ArrayList<>();

    public int getOpenCount() { return openCount; }
    public void setOpenCount(int openCount) { this.openCount = openCount; }

    public int getOngoingCount() { return ongoingCount; }
    public void setOngoingCount(int ongoingCount) { this.ongoingCount = ongoingCount; }

    public int getResolvedCount() { return resolvedCount; }
    public void setResolvedCount(int resolvedCount) { this.resolvedCount = resolvedCount; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public Double getAvgResolutionHoursOverall() { return avgResolutionHoursOverall; }
    public void setAvgResolutionHoursOverall(Double avgResolutionHoursOverall) {
        this.avgResolutionHoursOverall = avgResolutionHoursOverall;
    }

    public List<UserStat> getPerUser() { return perUser; }
    public void setPerUser(List<UserStat> perUser) { this.perUser = perUser != null ? perUser : new ArrayList<>(); }

    public List<TeamStat> getPerTeam() { return perTeam; }
    public void setPerTeam(List<TeamStat> perTeam) { this.perTeam = perTeam != null ? perTeam : new ArrayList<>(); }

    /** Carico di lavoro di un singolo utente, come mostrato nella dashboard. */
    public static class UserStat {
        private String name;
        private String email;
        private int assignedCount;
        private int openCount;
        private Double avgResolutionHours;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public int getAssignedCount() { return assignedCount; }
        public void setAssignedCount(int assignedCount) { this.assignedCount = assignedCount; }

        public int getOpenCount() { return openCount; }
        public void setOpenCount(int openCount) { this.openCount = openCount; }

        public Double getAvgResolutionHours() { return avgResolutionHours; }
        public void setAvgResolutionHours(Double avgResolutionHours) { this.avgResolutionHours = avgResolutionHours; }
    }

    /** Carico di lavoro di un singolo team, come mostrato nella dashboard. */
    public static class TeamStat {
        private String team;
        private int memberCount;
        private int assignedCount;
        private int openCount;
        private Double avgResolutionHours;

        public String getTeam() { return team; }
        public void setTeam(String team) { this.team = team; }

        public int getMemberCount() { return memberCount; }
        public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

        public int getAssignedCount() { return assignedCount; }
        public void setAssignedCount(int assignedCount) { this.assignedCount = assignedCount; }

        public int getOpenCount() { return openCount; }
        public void setOpenCount(int openCount) { this.openCount = openCount; }

        public Double getAvgResolutionHours() { return avgResolutionHours; }
        public void setAvgResolutionHours(Double avgResolutionHours) { this.avgResolutionHours = avgResolutionHours; }
    }
}
