package it.unina.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueDTO {
    private Integer id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private IssueType type;
    private byte[] image;
    private List<String> tags;
    private OffsetDateTime createdAt;
    private OffsetDateTime doneAt;
    private UserDTO creator;
    private UserDTO assignee;
    private ProjectDTO project;

    public IssueDTO(Integer id, String title, String description, Priority priority,
                    Status status, IssueType type, byte[] image, String tags,
                    OffsetDateTime createdAt, OffsetDateTime doneAt, UserDTO creator,
                    UserDTO assignee, ProjectDTO project) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.type = type;
        this.image = image;
        this.tags = Arrays.asList(tags.split(","));
        this.createdAt = createdAt;
        this.doneAt = doneAt;
        this.creator = creator;
        this.assignee = assignee;
        this.project = project;
    }
}