package com.example.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;

@Entity
@Table(name = "user_table")
@Data
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
    private boolean suspended;

    @JoinTable(name = "asset_table")
    @Formula(value = "(SELECT COUNT(*) FROM asset_table a WHERE a.user_id=id)")
    private int numberOfAssets;

    public User() {
        this.role = Role.USER;
        this.enabled = false;
        this.setJoinDate(LocalDate.now());
        this.suspended = false;
    }
}

