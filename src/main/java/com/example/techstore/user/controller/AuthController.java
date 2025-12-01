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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${application.endpoint.auth.root}")
public class AuthController {
    private final AuthService authService;
    private final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @PostMapping(path = "${application.endpoint.auth.sign-in}")
    public ResponseEntity<JwtResponse> createAuthToken(@RequestBody @Valid JwtRequest authRequest, BindingResult bindingResult) {

        LOG.info("Received auth request: {}", authRequest);

        if (bindingResult.hasErrors()) {
            String errorMsg = ErrorsUtil.getErrorMsg(bindingResult);
            throw new AuthException(errorMsg);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.createAuthToken(authRequest));
    }

    @PostMapping(path = "${application.endpoint.auth.sign-up}")
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