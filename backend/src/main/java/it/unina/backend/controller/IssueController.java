package it.unina.backend.controller;

import it.unina.backend.dto.IssueDTO;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.ok(dao.getAllIssues());
    }
}