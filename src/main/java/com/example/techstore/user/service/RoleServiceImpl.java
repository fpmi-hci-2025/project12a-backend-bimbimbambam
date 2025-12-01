package com.example.techstore.user.service;

import com.example.techstore.user.model.Role;
import com.example.techstore.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    @Cacheable(value = "roles", key = "'user'")
    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
    }

    @Override
    @Cacheable(value = "roles", key = "'admin'")
    public Role getAdminRole() {
        return roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
    }
}