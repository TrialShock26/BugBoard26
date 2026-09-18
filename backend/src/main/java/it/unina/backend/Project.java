package it.unina.backend;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Project {

    private final String id;
    private final String name;
    private final List<String> teams;
    private final long createdAt;

    public Project(String id, String name, List<String> teams, long createdAt) {
        this.id = id;
        this.name = name;
        this.teams = teams == null ? new ArrayList<>() : new ArrayList<>(teams);
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getTeams() { return teams; }
    public long getCreatedAt() { return createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("teams", teams);
        m.put("createdAt", createdAt);
        return m;
    }
}
