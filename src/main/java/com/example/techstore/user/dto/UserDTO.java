package com.example.techstore.user.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private List<String> roles;
}
