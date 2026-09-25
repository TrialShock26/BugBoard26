package it.unina.backend.controller;

import it.unina.backend.dao.IssueDAO;
import it.unina.backend.dto.IssueDTO;
import it.unina.backend.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}