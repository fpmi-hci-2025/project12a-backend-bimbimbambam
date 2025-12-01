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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserService userService;
    @Mock private CustomUserDetailsService customUserDetailsService;
    @Mock private JwtTokenUtils jwtTokenUtils;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void createAuthToken_Success() {
        JwtRequest request = new JwtRequest("user", "pass");
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(customUserDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtTokenUtils.generateToken(userDetails)).thenReturn("token123");

        JwtResponse response = authService.createAuthToken(request);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void createAuthToken_BadCredentials() {
        JwtRequest request = new JwtRequest("user", "wrong_pass");
        doThrow(new BadCredentialsException("Bad creds"))
                .when(authenticationManager).authenticate(any());

        assertThrows(AuthException.class, () -> authService.createAuthToken(request));
    }

    @Test
    void createNewUser_Success() {
        SaveUserDTO saveUserDTO = new SaveUserDTO();
        saveUserDTO.setUsername("newuser");
        saveUserDTO.setEmail("new@mail.com");
        saveUserDTO.setPassword("pass");
        saveUserDTO.setConfirmPassword("pass");

        when(userService.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userService.findByEmail("new@mail.com")).thenReturn(Optional.empty());

        User userEntity = new User();
        when(modelMapper.map(saveUserDTO, User.class)).thenReturn(userEntity);

        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setUsername("newuser");
        when(userService.createNewUser(userEntity)).thenReturn(expectedDTO);

        UserDTO result = authService.createNewUser(saveUserDTO);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
    }

    @Test
    void createNewUser_PasswordMismatch() {
        SaveUserDTO saveUserDTO = new SaveUserDTO();
        saveUserDTO.setPassword("pass1");
        saveUserDTO.setConfirmPassword("pass2");

        assertThrows(UserException.class, () -> authService.createNewUser(saveUserDTO));
        verify(userService, never()).createNewUser(any());
    }

    @Test
    void createNewUser_UsernameExists() {
        SaveUserDTO saveUserDTO = new SaveUserDTO();
        saveUserDTO.setUsername("exist");
        saveUserDTO.setPassword("pass");
        saveUserDTO.setConfirmPassword("pass");

        when(userService.findByUsername("exist")).thenReturn(Optional.of(new User()));

        assertThrows(UserException.class, () -> authService.createNewUser(saveUserDTO));
    }
}