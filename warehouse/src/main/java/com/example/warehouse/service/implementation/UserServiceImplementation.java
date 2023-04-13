package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.model.User;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.UserService;
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
    public boolean createUser(NewUserDTO newUserDTO) {
        User newUser = modelMapper.map(newUserDTO, User.class);
        try{
            userRepository.save(newUser);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public UserDTO findUserById(int id) {
        return modelMapper.map(userRepository.findById(id), UserDTO.class);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return modelMapper.map(userRepository.findAll(), new TypeToken<List<UserDTO>>(){}.getType());
    }

    @Override
    public boolean updateUser(UserDTO userDTO) {
        User editUser = userRepository.findById(userDTO.getId()).orElse(null);
        if(editUser == null)
            return false;

        editUser.setDateOfBirth(userDTO.getDateOfBirth());
        editUser.setBiography(userDTO.getBiography());
        editUser.setInterests(userDTO.getInterests());
        editUser.setName(userDTO.getName());

        try{
            userRepository.save(editUser);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public boolean deleteUser(int id) {
        try{
            userRepository.deleteById(id);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
