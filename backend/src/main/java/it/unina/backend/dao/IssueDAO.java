package it.unina.backend.dao;

import it.unina.backend.dto.IssueDTO;
import it.unina.backend.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface IssueDAO {
    List<IssueDTO> getAllIssues(String email, Map<String, String> requestParams);
    void newIssue(IssueDTO dto, String email);
    boolean handleIssue(int id, String email);
    void resolveIssue(int id);
    byte[] getImage(int id);
    List<UserDTO> getSuggestion();
}