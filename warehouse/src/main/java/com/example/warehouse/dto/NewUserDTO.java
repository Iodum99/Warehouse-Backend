package com.example.warehouse.dto;

import lombok.Data;

@Data
public class NewUserDTO {

    private String username;
    private String password;
    private String email;

    public NewUserDTO(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
