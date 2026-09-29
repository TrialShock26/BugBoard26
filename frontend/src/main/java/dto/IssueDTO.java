package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class IssueDTO {

    @Setter private String id;
    @Setter private String title;
    @Setter private String description;
    @Setter private IssueType type;
    @Setter private IssuePriority priority;
    @Setter private IssueStatus status;


    @Setter private UserDTO assignee;

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<>();

    public List<String> getTags() { return tags; }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public String getAssigneeEmail() {
        return assignee == null ? null : assignee.getEmail();
    }

    public boolean isAssigned() { return getAssigneeEmail() != null; }
}
