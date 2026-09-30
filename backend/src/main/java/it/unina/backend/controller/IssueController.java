package it.unina.backend.controller;

import it.unina.backend.dao.IssueDAO;
import it.unina.backend.dto.*;
import it.unina.backend.dto.IssueType;
import org.springframework.beans.factory.annotation.Value;
import it.unina.backend.service.IssueService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/issues")
public class IssueController {
    private IssueDAO dao;
    private IssueService service;

    @Value("${top.n}")
    private int topN;

    public IssueController(IssueDAO dao, IssueService service) {
        this.dao = dao;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<IssueDTO>> getAllIssues(@RequestParam Map<String, String> requestParams) {
        List<IssueDTO> result = dao.getAllIssues(SecurityContextHolder.getContext().getAuthentication().getName(), requestParams);
        if (result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }

    @Data
    @NoArgsConstructor
    public static class NewIssueDTO {
        private String title;
        private String description;
        private IssueType type;
        private Priority priority;
        private Integer projectId;
        private List<String> tags;
        private byte[] image;

        public NewIssueDTO(String title, String description, IssueType type,
                           Priority priority, Integer projectId, List<String> tags) {
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.type = type;
            this.image = image == null ? null : Base64.getDecoder().decode(image);
            this.tags = tags;
            this.projectId = projectId;
        }
    }
    @PostMapping
    public ResponseEntity<Void> newIssue(@RequestBody NewIssueDTO dto) {
        dao.newIssue(dto, SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/handle")
    public ResponseEntity<Void> handleIssue(@PathVariable int id) {
        if (!dao.handleIssue(id, SecurityContextHolder.getContext().getAuthentication().getName())) return ResponseEntity.badRequest().build();
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

    @GetMapping("/suggestion")
    public ResponseEntity<Boolean> calculateSuggestions() {
        return ResponseEntity.ok(service.getSuggestion(
                SecurityContextHolder.getContext().getAuthentication().getName(),
                topN
        ));
    }
}