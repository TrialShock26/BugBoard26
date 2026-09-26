package it.unina.backend.dao;

import it.unina.backend.dto.IssueDTO;

import java.util.List;

public interface IssueDAO {
    List<IssueDTO> getAllIssues(String email);
    void newIssue(IssueDTO dto, String email);
    void handleIssue(int id, String email);
    void resolveIssue(int id);
    byte[] getImage(int id);
}