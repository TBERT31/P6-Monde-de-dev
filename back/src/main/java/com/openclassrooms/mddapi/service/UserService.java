package com.openclassrooms.mddapi.service;


import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les utilisateurs.
 */
public interface UserService {
    Optional<User> getUserById(Long id);
    Optional<User> getUserByIdWithAuthorization(Long id, String emailJwt);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByEmailWithAuthorization(String email, String emailJwt);
    Optional<User> getUserByUsername(String username);
    User updateEmailOrUsername(Long id, String newEmail, String newUsername, String emailJwt);
    User registerUser(User user);
    Optional<User> getUserByEmailOrUsername(String emailOrUsername);
}
