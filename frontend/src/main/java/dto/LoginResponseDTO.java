package dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private UserDTO user;
    private String token;
}
