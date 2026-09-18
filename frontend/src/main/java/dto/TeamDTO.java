package dto;

public class TeamDTO {

    private String id;
    private String name;
    private String project;

    public TeamDTO() { }

    public TeamDTO(String id, String name, String project) {
        this.id = id;
        this.name = name;
        this.project = project;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }
}
