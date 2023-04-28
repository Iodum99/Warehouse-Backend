package com.example.warehouse.repository;

import com.example.warehouse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsernameContainingIgnoreCase(String username);
    Optional<User> findByEmailContainingIgnoreCase(String email);
}

