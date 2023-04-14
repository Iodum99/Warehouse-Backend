package com.example.warehouse.dto;

import com.example.warehouse.model.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {

    private int id;
    private String username;
    private String password;
    private String email;
    private String name;
    private String biography;
    private String interests;
    private LocalDate dateOfBirth;
    private String avatarPath;
    private boolean enabled;
    private Role role;
}
