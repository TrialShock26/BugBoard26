package dto;

public class UserDTO {

    private String id;
    private String token;
    private String email;
    private String name;
    private UserRole role;
    private String team;

    public UserDTO() { }

    public UserDTO(String id, String token, String email, String name, UserRole role, String team) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
        this.team = team;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isAdmin() { return role == UserRole.ADMIN; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
}
