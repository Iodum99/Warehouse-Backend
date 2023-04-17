package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.exception.UserEmailExistsException;
import com.example.warehouse.exception.UserNotFoundException;
import com.example.warehouse.exception.UserUsernameExistsException;
import com.example.warehouse.model.Role;
import com.example.warehouse.model.User;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    ModelMapper modelMapper = new ModelMapper();
    private final UserRepository userRepository;

    @Override
    public void createUser(NewUserDTO newUserDTO) {
        User newUser = modelMapper.map(newUserDTO, User.class);
        validate(newUser);
        newUser.setRole(Role.USER);
        newUser.setEnabled(false);
        newUser.setPassword(passwordEncoder().encode(newUser.getPassword()));
        //TODO: Create verification Token and send Email
        userRepository.save(newUser);
    }

    private void validate(User user){

        if(userRepository.findByUsername(user.getUsername()) != null)
            throw new UserUsernameExistsException("Username: " + user.getUsername());

        if(userRepository.findByEmail(user.getEmail()) != null)
            throw new UserEmailExistsException("E-mail: " + user.getEmail());

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
        else throw new UserNotFoundException("Id: " + id);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void enableUser(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Id: " + id));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
