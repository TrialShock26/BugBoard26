package it.unina.backend.dao;

import it.unina.backend.dto.UserDTO;

import java.util.Optional;

public interface UserDAO {
    Optional<UserDTO> login(String email);
}