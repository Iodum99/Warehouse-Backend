package com.example.warehouse.repository;

import com.example.warehouse.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsernameContainingIgnoreCase(String username);
    Optional<User> findByEmailContainingIgnoreCase(String email);
    @Query("select u from User u where u.enabled = true")
    List<Optional<User>> findAllEnabledUsers(Sort sort);
}

