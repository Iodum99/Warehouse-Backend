package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.exception.UserNotFoundException;
import com.example.warehouse.model.User;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.UserService;
import com.sun.jdi.InternalException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    UserRepository userRepository;

    @Override
    public void createUser(NewUserDTO newUserDTO) {
        User newUser = modelMapper.map(newUserDTO, User.class);
        userRepository.save(newUser);
    }

    @Override
    public UserDTO findUserById(int id) {
        return modelMapper.map(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("ID: " + id)), UserDTO.class);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return modelMapper.map(userRepository.findAll(), new TypeToken<List<UserDTO>>(){}.getType());
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        User editUser = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        editUser.setDateOfBirth(userDTO.getDateOfBirth());
        editUser.setBiography(userDTO.getBiography());
        editUser.setInterests(userDTO.getInterests());
        editUser.setName(userDTO.getName());

        userRepository.save(editUser);

    }

    @Override
    public void deleteUser(int id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
        else throw new UserNotFoundException("ID: " + id);
    }

}
