package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("userId")
    private String id;
    private String email;
    @JsonProperty("type")
    private UserRole role;

    public boolean isAdmin() { return role == UserRole.ADMIN; }
}
