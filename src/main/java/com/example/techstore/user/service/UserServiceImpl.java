package com.example.techstore.user.service;

import com.example.techstore.user.dto.SaveUserDTO;
import com.example.techstore.user.dto.UserDTO;
import com.example.techstore.user.model.Role;
import com.example.techstore.user.model.User;
import com.example.techstore.user.repository.UserRepository;
import com.example.techstore.common.util.JwtTokenUtils;
import com.example.techstore.user.util.UserException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    @CachePut(value = "userById", key = "#result.id")
    public UserDTO createNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return convertToUserDTO(savedUser);
    }

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToUserDTO);
    }

    @Override
    @Cacheable(value = "userById", key = "#id")
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserException(String.format("User %s not found", id)));
        return convertToUserDTO(user);
    }

    @Override
    @Transactional
    @CachePut(value = "userById", key = "#id")
    public UserDTO updateUserById(Long id, SaveUserDTO updatedUser, String token) {
        Long currentUserId = jwtTokenUtils.getUserId(token);
        List<String> roles = jwtTokenUtils.getRoles(token);

        if (!roles.contains("ROLE_ADMIN") && !currentUserId.equals(id)) {
            throw new UserException("You can only update your own account.");
        }

        if (!updatedUser.getPassword().equals(updatedUser.getConfirmPassword())) {
            throw new UserException("Incorrect password!");
        }

        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new UserException(String.format("User %s not found", id)));

        Optional<User> userByUsername = userRepository.findByUsername(updatedUser.getUsername());
        if(userByUsername.isPresent() && !userByUsername.get().getId().equals(id)) {
            throw new UserException("Username already taken");
        }
        Optional<User> userByEmail = userRepository.findByEmail(updatedUser.getEmail());
        if(userByEmail.isPresent() && !userByEmail.get().getId().equals(id)) {
            throw new UserException("Email already taken");
        }

        enrichPropertyForUpdate(existingUser, convertSaveUserDTOToUser(updatedUser));

        User savedUser = userRepository.save(existingUser);
        return convertToUserDTO(savedUser);
    }

    @Override
    @Transactional
    @CachePut(value = "userById", key = "#userId")
    public UserDTO assignAdminRole(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserException("User not found"));

        Role adminRole = roleService.getAdminRole();

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals(adminRole.getName()))) {
            throw new UserException("User already has ADMIN role");
        }

        user.getRoles().add(adminRole);
        User savedUser = userRepository.save(user);

        return convertToUserDTO(savedUser);
    }

    @Override
    public Boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userById", key = "#id")
    public void deleteUserById(Long id, String token) {
        Long currentUserId = jwtTokenUtils.getUserId(token);
        List<String> roles = jwtTokenUtils.getRoles(token);

        if (!roles.contains("ROLE_ADMIN") && !currentUserId.equals(id)) {
            throw new UserException("You can only delete your own account.");
        }

        if (!userRepository.existsById(id)) {
            throw new UserException("User not found");
        }

        userRepository.deleteById(id);
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private User convertSaveUserDTOToUser(SaveUserDTO saveUserDTO){
        return modelMapper.map(saveUserDTO, User.class);
    }

    private void enrichPropertyForUpdate(User existingUser, User updatedUser) {
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setUpdatedAt(LocalDateTime.now());
    }
}