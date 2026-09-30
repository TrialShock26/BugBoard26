package it.unina.backend.service;

import it.unina.backend.dao.IssueDAO;
import it.unina.backend.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {
    private IssueDAO dao;

    public IssueService(IssueDAO dao) {
        this.dao = dao;
    }

    public boolean getSuggestion(String email, int topN) {
        List<UserDTO> result = dao.getSuggestion(topN);
        for (UserDTO user : result) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
}