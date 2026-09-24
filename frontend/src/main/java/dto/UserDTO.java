package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String token;
    private String email;
    private String name;
    private UserRole role;
    private String team;

    public boolean isAdmin() { return role == UserRole.ADMIN; }
}
