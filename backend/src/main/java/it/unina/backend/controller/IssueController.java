package it.unina.backend.controller;

import it.unina.backend.dao.IssueDAO;
import it.unina.backend.dto.IssueDTO;
import it.unina.backend.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issues")
public class IssueController {
    private IssueDAO dao;

    public IssueController(IssueDAO dao) {
        this.dao = dao;
    }

    @GetMapping
    public ResponseEntity<List<IssueDTO>> getAllIssues() {
        List<IssueDTO> result = dao.getAllIssues(SecurityContextHolder.getContext().getAuthentication().getName());
        if (result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> newIssue(@RequestBody IssueDTO dto) {
        dao.newIssue(dto, SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/handle")
    public ResponseEntity<Void> handleIssue(@PathVariable int id) {
        dao.handleIssue(id, SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveIssue(@PathVariable int id) {
        dao.resolveIssue(id);
        return ResponseEntity.ok().build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageDTO {
        byte[] image;
    }
    @GetMapping("/{id}/image")
    public ResponseEntity<ImageDTO> getIssueImage(@PathVariable int id) {
        return ResponseEntity.ok(new ImageDTO(dao.getImage(id)));
    }
}