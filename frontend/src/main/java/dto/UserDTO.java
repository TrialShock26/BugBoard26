package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rispecchia UserDTO del backend reale: campi userId/email/hashedPassword/type.
 * hashedPassword non viene mai esposto al frontend (il login lo sovrascrive
 * server-side con un placeholder), quindi qui non lo teniamo. Non esistono
 * "name" o "team" sull'utente: non sono colonne della tabella User_ nello
 * schema SQL.
 */
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
