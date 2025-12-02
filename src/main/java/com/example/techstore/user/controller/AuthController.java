package com.example.techstore.user.controller;

import com.example.techstore.common.util.ErrorsUtil;
import com.example.techstore.user.dto.JwtRequest;
import com.example.techstore.user.dto.JwtResponse;
import com.example.techstore.user.dto.SaveUserDTO;
import com.example.techstore.user.dto.UserDTO;
import com.example.techstore.user.service.AuthService;
import com.example.techstore.user.util.AuthException;
import com.example.techstore.user.util.UserException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtResponse> createAuthToken(@RequestBody @Valid JwtRequest authRequest, BindingResult bindingResult) {

        log.info("Received auth request for user: {}", authRequest.getUsername());

        if (bindingResult.hasErrors()) {
            String errorMsg = ErrorsUtil.getErrorMsg(bindingResult);
            throw new AuthException(errorMsg);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.createAuthToken(authRequest));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> createNewUser(@RequestBody @Valid SaveUserDTO saveUserDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMsg = ErrorsUtil.getErrorMsg(bindingResult);
            throw new UserException(errorMsg);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.createNewUser(saveUserDTO));
    }
}