package it.unina.backend;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class User {

    public enum Role { ADMIN, DEV }

    public enum Team { FRONTEND, BACKEND, MOBILE, QA }

    private final String id;
    private final String email;
    private final String password;

    private final String name;
    private final Role role;
    private final Team team;


    private final List<Map<String, String>> projectChoices = new ArrayList<>();

    public User(String id, String email, String password, String name, Role role, Team team) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.team = team;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public Team getTeam() { return team; }

    public List<Map<String, String>> getProjectChoices() { return projectChoices; }

    public void chooseProject(String projectId, String projectTeam) {
        for (Map<String, String> choice : projectChoices) {
            if (choice.get("projectId").equals(projectId)) {
                choice.put("team", projectTeam);
                return;
            }
        }
        Map<String, String> choice = new LinkedHashMap<>();
        choice.put("projectId", projectId);
        choice.put("team", projectTeam);
        projectChoices.add(choice);
    }

    public boolean leaveProject(String projectId) {
        return projectChoices.removeIf(c -> c.get("projectId").equals(projectId));
    }

    public boolean checkPassword(String candidate) {
        return candidate != null && password.equals(candidate);
    }

    public Map<String, Object> toPublicMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("email", email);
        m.put("name", name);
        m.put("role", role.name());
        m.put("team", team == null ? null : team.name());
        m.put("projectChoices", projectChoices);
        return m;
    }
}

