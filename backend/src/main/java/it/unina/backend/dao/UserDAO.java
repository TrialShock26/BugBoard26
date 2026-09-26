package it.unina.backend.dao;

import it.unina.backend.dto.*;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<UserDTO> login(String email);
    void createProject(String name, String teamNames);
    List<ProjectDTO> getProjects();
    List<TeamDTO> getTeams(int id);
    void joinTeam(int id, String email);
    void newUser(String email, String hashedPassword, UserType type);
}