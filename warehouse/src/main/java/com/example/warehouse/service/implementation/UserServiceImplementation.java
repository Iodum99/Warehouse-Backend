package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.exception.UserEmailExistsException;
import com.example.warehouse.exception.UserNotFoundException;
import com.example.warehouse.exception.UserUsernameExistsException;
import com.example.warehouse.model.Role;
import com.example.warehouse.model.User;
import com.example.warehouse.model.VerificationToken;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.repository.VerificationTokenRepository;
import com.example.warehouse.service.EmailService;
import com.example.warehouse.service.UserService;
import com.example.warehouse.service.VerificationTokenService;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final ServletContext context;

    @Override
    public void createUser(NewUserDTO newUserDTO){
        User newUser = modelMapper.map(newUserDTO, User.class);
        validate(newUser);
        newUser.setRole(Role.USER);
        newUser.setEnabled(false);
        newUser.setPassword(passwordEncoder().encode(newUser.getPassword()));
        User createdUser = userRepository.save(newUser);
        VerificationToken createdToken = verificationTokenRepository.save(new VerificationToken(createdUser.getId()));
        emailService.sendVerificationEmail(newUser.getEmail(), createdToken.getId().toString());
        createUserDirectory(createdUser.getId());
    }

    private void validate(User user){

        if(userRepository.findByUsername(user.getUsername()) != null)
            throw new UserUsernameExistsException("Username: " + user.getUsername());

        if(userRepository.findByEmail(user.getEmail()) != null)
            throw new UserEmailExistsException("E-mail: " + user.getEmail());

    }

    private void createUserDirectory(int id) {
        try {
            Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/assets/user_id_" + id));
        } catch (Exception e){
            e.printStackTrace();
        }
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

    @Override
    public void initialize() {
        createUser(new NewUserDTO("Admin", "123", "test1@email.com"));
        createUser(new NewUserDTO("Decla", "123", "test2@email.com"));
        createUser(new NewUserDTO("Test3", "123", "test3@email.com"));
        createUser(new NewUserDTO("Test4", "123", "test4@email.com"));
    }
}
