package com.example.techstore.user.service;

import com.example.techstore.common.util.JwtTokenUtils;
import com.example.techstore.user.dto.JwtRequest;
import com.example.techstore.user.dto.JwtResponse;
import com.example.techstore.user.dto.SaveUserDTO;
import com.example.techstore.user.dto.UserDTO;
import com.example.techstore.user.model.User;
import com.example.techstore.user.security.CustomUserDetails;
import com.example.techstore.user.security.CustomUserDetailsService;
import com.example.techstore.user.util.AuthException;
import com.example.techstore.user.util.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    @Override
    public JwtResponse createAuthToken(JwtRequest authRequest) {
        log.info("Attempting to authenticate user: {}", authRequest.getUsername());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", authRequest.getUsername(), e);
            throw new AuthException("Incorrect login or password!");
        }

        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService
                .loadUserByUsername(authRequest.getUsername());

        String token = jwtTokenUtils.generateToken(userDetails);

        return new JwtResponse(token);
    }

    @Override
    @Transactional
    public UserDTO createNewUser(SaveUserDTO saveUserDTO) {
        if (!saveUserDTO.getPassword().equals(saveUserDTO.getConfirmPassword())) {
            throw new UserException("Incorrect password!");
        }

        if (userService.findByUsername(saveUserDTO.getUsername()).isPresent()) {
            throw new UserException("User with username " + saveUserDTO.getUsername() + " already exist");
        }

        if (userService.findByEmail(saveUserDTO.getEmail()).isPresent()) {
            throw new UserException("Email " + saveUserDTO.getEmail() + " address already in use!");
        }

        User user = convertRegistrationUserDTOToUser(saveUserDTO);
        return userService.createNewUser(user);
    }

    private User convertRegistrationUserDTOToUser(SaveUserDTO saveUserDTO) {
        return modelMapper.map(saveUserDTO, User.class);
    }
}