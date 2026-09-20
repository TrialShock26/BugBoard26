package it.unina.backend.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.unina.backend.dao.UserDAO;
import it.unina.backend.dto.UserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@RestController
public class AuthController {
    private UserDAO dao;
    private PasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String secret;

    public AuthController(UserDAO dao, PasswordEncoder encoder) {
        this.dao = dao;
        this.encoder = encoder;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponseDTO {
        private UserDTO user;
        private String token;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Optional<UserDTO> optUser = dao.login(request.getEmail());

        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }

        UserDTO user = optUser.get();
        if (!encoder.matches(request.getPassword(), user.getHashedPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }

        String role = user.getType().toString();
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
        user.setHashedPassword("Inaccessible data");
        return ResponseEntity.ok(new AuthResponseDTO(user, token));
    }
}