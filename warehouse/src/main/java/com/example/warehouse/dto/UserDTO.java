package com.example.warehouse.dto;

import com.example.warehouse.model.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {

    private int id;
    private String username;
    private String password;
    private String newPassword;
    private String email;
    private String name;
    private String surname;
    private String biography;
    private String interests;
    private String country;
    private LocalDate joinDate;
    private String avatar;
    private boolean enabled;
    private Role role;
    private int numberOfAssets;
    private boolean suspended;
}
