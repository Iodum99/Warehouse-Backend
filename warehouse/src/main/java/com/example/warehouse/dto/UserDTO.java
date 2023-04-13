package com.example.warehouse.dto;

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
}
