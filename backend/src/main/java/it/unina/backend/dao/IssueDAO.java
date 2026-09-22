package it.unina.backend.dao;

import it.unina.backend.dto.IssueDTO;

import java.util.List;

public interface IssueDAO {
    List<IssueDTO> getAllIssues(String email);
}