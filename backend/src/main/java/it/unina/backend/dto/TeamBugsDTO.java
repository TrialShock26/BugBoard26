package it.unina.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamBugsDTO {
    private TeamDTO team;
    private Integer bugs;
}