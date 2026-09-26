package it.unina.backend.controller;

import it.unina.backend.dao.UserDAO;
import it.unina.backend.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class UserController {
    private final PasswordEncoder encoder;
    private UserDAO dao;

    public UserController(UserDAO dao, PasswordEncoder encoder) {
        this.dao = dao;
        this.encoder = encoder;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewProjectDTO {
        @NotBlank(message = "Please enter a name.")
        private String name;
        @NotEmpty(message = "Please enter at least one team name.")
        private Set<@NotBlank(message = "Please enter valid team name(s).") String> teamNames;
    }
    @PostMapping("/projects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createProject(@Valid @RequestBody NewProjectDTO request) {
        dao.createProject(request.getName(), String.join(",", request.getTeamNames())+",");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO>> getProjects() {
        List<ProjectDTO> result = dao.getProjects();
        if (result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/projects/{id}/teams")
    public ResponseEntity<List<TeamDTO>> getTeams(@PathVariable int id) {
        List<TeamDTO> result = dao.getTeams(id);
        if (result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/projects/{projectId}/teams/{teamId}")
    public ResponseEntity<Void> joinTeam(@PathVariable("teamId") int teamId) {
        dao.joinTeam(teamId, SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok().build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewUserDTO {
        @NotBlank
        String email;
        @NotBlank
        String password;
        @NotNull
        UserType type;
    }
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> newUser(@Valid @RequestBody NewUserDTO request) {
        dao.newUser(request.getEmail(), encoder.encode(request.getPassword()), request.getType());
        return ResponseEntity.ok().build();
    }
}