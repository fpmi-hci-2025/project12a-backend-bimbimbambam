package com.example.techstore.user.controller;

import com.example.techstore.common.util.ErrorsUtil;
import com.example.techstore.common.util.JwtTokenUtils;
import com.example.techstore.user.dto.SaveUserDTO;
import com.example.techstore.user.dto.UserDTO;
import com.example.techstore.user.service.UserService;
import com.example.techstore.user.util.UserException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findAll(pageable));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenUtils.getUserId(token.replace("Bearer ", ""));
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                              @PathVariable("id") Long id, @RequestBody @Valid SaveUserDTO saveUserDTO, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            String errorMsg = ErrorsUtil.getErrorMsg(bindingResult);
            throw new UserException(errorMsg);
        }

        String jwtToken = authorizationHeader.replace("Bearer ", "");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.updateUserById(id, saveUserDTO, jwtToken));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable("id") Long id) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        userService.deleteUserById(id, jwtToken);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.getUserById(id));
    }

    @PostMapping("/{id}/assign-admin")
    public ResponseEntity<UserDTO> assignAdminRole(@PathVariable("id") Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.assignAdminRole(id));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> userExists(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.existsById(id));
    }
}