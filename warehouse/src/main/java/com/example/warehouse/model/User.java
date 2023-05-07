package com.example.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

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

    @JoinTable(name = "asset_table")
    @Formula(value = "(SELECT COUNT(*) FROM asset_table a WHERE a.user_id=id)")
    private int numberOfAssets;
}
