package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** GET/POST /projects: il backend chiama l'id "projectId", non "id". */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    @JsonProperty("projectId")
    private String id;
    private String name;
}
