package it.unina.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.unina.backend.dao.UserDAO;
import it.unina.backend.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    private UserDAO dao;
    private PasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String secret;

    public AuthService(UserDAO dao, PasswordEncoder encoder) {
        this.dao = dao;
        this.encoder = encoder;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponseDTO {
        private UserDTO user;
        private String token;
    }
    public AuthResponseDTO login(String email, String password) {
        Optional<UserDTO> optUser = dao.login(email);

        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }

        UserDTO user = optUser.get();
        if (!encoder.matches(password, user.getHashedPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }

        String role = user.getType().toString();
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(8, ChronoUnit.HOURS)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
        user.setHashedPassword("Inaccessible data");
        return new AuthResponseDTO(user, token);
    }
}
