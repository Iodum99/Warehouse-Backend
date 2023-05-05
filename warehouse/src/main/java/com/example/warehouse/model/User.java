package com.example.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_table")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
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

    public User(String username,
                String password,
                String email,
                String name,
                String surname,
                String biography,
                String interests,
                String country,
                String avatar,
                Role role,
                LocalDate joinDate,
                boolean enabled
                ){
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.biography = biography;
        this.interests = interests;
        this.country = country;
        this.avatar = avatar;
        this.enabled = enabled;
        this.role = role;
        this.joinDate = joinDate;
    }

}
