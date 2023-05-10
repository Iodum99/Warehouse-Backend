package com.example.warehouse.service;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.model.User;
import com.example.warehouse.model.helper.UserSearchRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    void createUser(NewUserDTO newUserDTO);
    UserDTO findUserById(int id);
    List<UserDTO> findAllUsersAdmin(UserSearchRequest userSearchRequest);
    void updateUser(UserDTO userDTO, MultipartFile image);
    void deleteUser(int id);
    User findUserByUsername(String username);
    void toggleUserStatus(int id);
    void toggleUserSuspension(int id);
    List<UserDTO> findAllEnabledUsers(UserSearchRequest userSearchRequest);
}
