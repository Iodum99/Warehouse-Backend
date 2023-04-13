package com.example.warehouse.service;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;

import java.util.List;

public interface UserService {

    boolean createUser(NewUserDTO newUserDTO);
    UserDTO findUserById(int id);
    List<UserDTO> findAllUsers();
    boolean updateUser(UserDTO userDTO);
    boolean deleteUser(int id);
}
