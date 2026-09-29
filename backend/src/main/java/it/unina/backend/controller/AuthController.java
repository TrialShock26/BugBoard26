package it.unina.backend.controller;

import it.unina.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequestDTO {
        @NotBlank(message = "Please enter an email address.")
        @Email(message = "Please enter a valid email address.")
        private String email;

        @NotBlank(message = "Please enter a password.")
        private String password;
    }
    @PostMapping("/auth/login")
    public ResponseEntity<UserService.AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(service.login(request.getEmail(), request.getPassword()));
    }
}