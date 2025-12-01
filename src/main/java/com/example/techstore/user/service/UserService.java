package com.example.techstore.user.service;

import com.example.techstore.user.dto.SaveUserDTO;
import com.example.techstore.user.dto.UserDTO;
import com.example.techstore.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    UserDTO createNewUser(User user);
    Page<UserDTO> findAll(Pageable pageable);
    UserDTO getUserById(Long id);
    UserDTO updateUserById(Long id, SaveUserDTO updatedUser, String token);
    UserDTO assignAdminRole(Long userId);
    Boolean existsById(Long id);
    void deleteUserById(Long id, String token);
}
