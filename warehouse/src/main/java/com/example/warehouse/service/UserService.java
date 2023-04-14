package com.example.warehouse.service;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;

import java.util.List;

public interface UserService {

    void createUser(NewUserDTO newUserDTO);
    UserDTO findUserById(int id);
    List<UserDTO> findAllUsers();
    void updateUser(UserDTO userDTO);
    void deleteUser(int id);
}
