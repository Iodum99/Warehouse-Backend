package com.example.warehouse.service;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.model.User;
import jakarta.mail.MessagingException;

import java.util.List;

public interface UserService {

    void createUser(NewUserDTO newUserDTO);
    UserDTO findUserById(int id);
    List<UserDTO> findAllUsers();
    void updateUser(UserDTO userDTO);
    void deleteUser(int id);
    User findUserByUsername(String username);
    void enableUser(int id);
    void initialize();
}
