package it.unina.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
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
    private Type type;
    private byte[] image;
    private List<String> tags;
    private OffsetDateTime createdAt;
    private OffsetDateTime doneAt;
    private UserDTO creator;
    private UserDTO assignee;
}