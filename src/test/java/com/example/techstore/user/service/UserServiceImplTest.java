package com.example.techstore.user.service;

import com.example.techstore.user.dto.UserDTO;
import com.example.techstore.user.model.Role;
import com.example.techstore.user.model.User;
import com.example.techstore.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createNewUser_ShouldEncodePasswordAndSave() {
        User inputUser = new User();
        inputUser.setPassword("rawPassword");

        Role userRole = new Role(1, "ROLE_USER");
        when(roleService.getUserRole()).thenReturn(userRole);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setPassword("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setId(1L);
        when(modelMapper.map(savedUser, UserDTO.class)).thenReturn(expectedDTO);

        UserDTO result = userService.createNewUser(inputUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> found = userService.findByUsername(username);

        assertTrue(found.isPresent());
        assertEquals(username, found.get().getUsername());
    }
}
