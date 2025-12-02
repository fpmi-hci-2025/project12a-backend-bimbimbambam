package com.example.techstore.user.service;

import com.example.techstore.user.model.Role;
import com.example.techstore.user.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void getUserRole_Success() {
        Role role = new Role();
        role.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        Role result = roleService.getUserRole();
        assertEquals("ROLE_USER", result.getName());
    }

    @Test
    void getAdminRole_Success() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));

        Role result = roleService.getAdminRole();
        assertEquals("ROLE_ADMIN", result.getName());
    }

    @Test
    void getUserRole_NotFound() {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> roleService.getUserRole());
    }
}