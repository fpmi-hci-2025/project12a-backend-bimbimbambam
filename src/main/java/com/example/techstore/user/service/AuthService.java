package com.example.techstore.user.service;

import com.example.techstore.user.dto.JwtRequest;
import com.example.techstore.user.dto.JwtResponse;
import com.example.techstore.user.dto.SaveUserDTO;
import com.example.techstore.user.dto.UserDTO;

public interface AuthService {

    JwtResponse createAuthToken(JwtRequest authRequest);
    UserDTO createNewUser(SaveUserDTO saveUserDTO);

}
