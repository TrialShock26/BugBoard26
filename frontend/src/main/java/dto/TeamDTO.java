package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/** GET /projects/{id}/teams: il backend chiama l'id "teamId", non "id". */
@Data
@NoArgsConstructor
public class TeamDTO {
    @JsonProperty("teamId")
    private String id;
    private String name;
    private String project;
}
