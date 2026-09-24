package it.unina.backend.controller;

import it.unina.backend.dao.UserDAO;
import it.unina.backend.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class UserController {
    private UserDAO dao;

    public UserController(UserDAO dao) {
        this.dao = dao;
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
        return ResponseEntity.ok(dao.getProjects());
    }

    @GetMapping("/projects/{id}/teams")
    public ResponseEntity<List<TeamDTO>> getTeams(@PathVariable int id) {
        return ResponseEntity.ok(dao.getTeams(id));
    }

    @PutMapping("/projects/{projectId}/teams/{teamId}")
    public ResponseEntity<Void> joinTeam(@PathVariable("teamId") int teamId) {
        dao.joinTeam(teamId, SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok().build();
    }
}