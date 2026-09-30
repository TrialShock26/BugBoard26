package it.unina.backend.service;

import it.unina.backend.dao.IssueDAO;
import it.unina.backend.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestIssueService {

    @Mock
    private IssueDAO dao;

    private IssueService service;

    @BeforeEach
    void setUp() {
        service = new IssueService(dao);
    }

    private UserDTO user(String email) {
        UserDTO u = new UserDTO();
        u.setEmail(email);
        return u;
    }

    @Test
    void getSuggestion_emptyList_returnFalse() {
        when(dao.getSuggestion(5)).thenReturn(List.of());

        assertFalse(service.getSuggestion("a@b.it", 5));
    }

    @Test
    void getSuggestion_emailToSuggest_returnTrue() {
        when(dao.getSuggestion(5)).thenReturn(List.of(user("x@y.it"), user("a@b.it")));

        assertTrue(service.getSuggestion("a@b.it", 5));
    }

    @Test
    void getSuggestion_emailNotToSuggest_returnFalse() {
        when(dao.getSuggestion(5)).thenReturn(List.of(user("x@y.it"), user("z@y.it")));

        assertFalse(service.getSuggestion("a@b.it", 5));
    }

    @Test
    void getSuggestion_emailFirstOccurrence_returnTrue() {
        when(dao.getSuggestion(3)).thenReturn(List.of(user("a@b.it"), user("x@y.it"), user("z@y.it")));

        assertTrue(service.getSuggestion("a@b.it", 3));
    }

    @Test
    void getSuggestion_emailLastOccurrence_returnTrue() {
        when(dao.getSuggestion(3)).thenReturn(List.of(user("x@y.it"), user("z@y.it"), user("a@b.it")));

        assertTrue(service.getSuggestion("a@b.it", 3));
    }

    @Test
    void getSuggestion_topNWorks() {
        when(dao.getSuggestion(7)).thenReturn(List.of());

        service.getSuggestion("a@b.it", 7);

        verify(dao).getSuggestion(7);
    }
}