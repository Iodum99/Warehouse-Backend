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
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private static final String DIRECTORY = "..\\..\\Warehouse-Frontend\\warehouse\\src\\assets\\user_id_%d";
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
        if(userRepository.findByUsernameContainingIgnoreCase(user.getUsername()).isPresent())
            throw new UserUsernameExistsException("Username: " + user.getUsername());

        if(userRepository.findByEmailContainingIgnoreCase(user.getEmail()).isPresent())
            throw new UserEmailExistsException("E-mail: " + user.getEmail());
    }

    private void createUserDirectory(int id) {
        try {
            Files.createDirectories(Paths.get(DIRECTORY.formatted(id)));
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
    public void updateUser(UserDTO userDTO, MultipartFile image) {
        User editUser = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        editUser.setDateOfBirth(userDTO.getDateOfBirth());
        editUser.setBiography(userDTO.getBiography());
        editUser.setInterests(userDTO.getInterests());
        editUser.setCountry(userDTO.getCountry());
        editUser.setName(userDTO.getName());
        editUser.setSurname(userDTO.getSurname());

        if(!userDTO.getPassword().equals(""))
            editUser.setPassword(passwordEncoder().encode(userDTO.getPassword()));

        try{
            if(image != null)
                editUser.setAvatar(image.getBytes());
        } catch (Exception e){
            e.printStackTrace();
        }

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
        return userRepository.findByUsernameContainingIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException("Username: " + username));
    }

    @Override
    public void enableUser(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Id: " + id));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void initialize() {
        try{
            byte[] img = Files.readAllBytes(
                    new File(System.getProperty("user.dir") + "\\assets\\default_avatar.png").toPath());
            User user1 = new User(
                    "Iodum",passwordEncoder().encode("ADMINsifra123"), "iodum@admin.com", "Dejan", "Bjelic",
                    "", "", "Serbia", LocalDate.of(1999, 4, 5),
                    img,Role.ADMIN, LocalDate.of(2023, 1, 1));
            User user2 = new User(
                    "Delca",passwordEncoder().encode("USERsifra123"), "delca@gmail.com", "Delca", "Britt",
                    "I am an introvert boy, born in late 1998! (Lie, I was born befire Christ and dinsaurs were my pets. I am the cause of the Big Bang, oops!)", "Graphics design, binging series, Eurovision and in free time I like to pretend I am the Qeen Elizabeth!",
                    "France", LocalDate.of(1998, 11, 26),
                    img,Role.USER, LocalDate.of(2023, 1, 2));

            User user3 = new User(
                    "MarlenaCrystal",passwordEncoder().encode("USERsifra123"), "marlena@gmail.com", "Marlena", "Crystal",
                    "80s Queen slaying in free time", "Video games, 80s music production",
                    "Germany", LocalDate.of(1997, 3, 9),
                    img,Role.USER, LocalDate.of(2023, 1, 2));

            User user4 = new User(
                    "Dermahn",passwordEncoder().encode("USERsifra123"), "dermahn@gmail.com", "Dominik", "Taylor",
                    "Skater boy and a punk", "Video games, watching series",
                    "Germany", LocalDate.of(1996, 8, 13),
                    img,Role.USER, LocalDate.of(2023, 1, 2));

            createUserDirectory(1);
            createUserDirectory(2);
            createUserDirectory(3);
            createUserDirectory(4);

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
