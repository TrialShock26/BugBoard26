package dto;

import lombok.Data;


@Data
public class ErrorResponseDTO {
    private String message;
    private String error;
    private String timestamp;
}
