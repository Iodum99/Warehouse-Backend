package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.exception.UserEmailExistsException;
import com.example.warehouse.exception.UserInvalidPasswordException;
import com.example.warehouse.exception.UserNotFoundException;
import com.example.warehouse.exception.UserUsernameExistsException;
import com.example.warehouse.model.User;
import com.example.warehouse.model.VerificationToken;
import com.example.warehouse.model.helper.UserSearchRequest;
import com.example.warehouse.repository.AssetRepository;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.repository.VerificationTokenRepository;
import com.example.warehouse.service.EmailService;
import com.example.warehouse.service.UserService;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private static final String DIRECTORY = "..\\..\\Warehouse-Frontend\\warehouse\\src\\assets\\user_id_%d";
    private static final String DEFAULT_AVATAR = "..\\..\\Warehouse-Frontend\\warehouse\\src\\assets\\default_avatar.png";
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    ModelMapper modelMapper = new ModelMapper();
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AssetRepository assetRepository;
    private final EmailService emailService;
    private final ServletContext context;

    @Override
    public void createUser(NewUserDTO newUserDTO){
        User newUser = modelMapper.map(newUserDTO, User.class);
        validate(newUser);
        newUser.setPassword(passwordEncoder().encode(newUser.getPassword()));
        newUser.setAvatar(DEFAULT_AVATAR);
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
    public List<UserDTO> findAllUsersAdmin(UserSearchRequest userSearchRequest) {
        return modelMapper.map(userRepository.findAll(getSpecification(userSearchRequest, true)),
                new TypeToken<List<UserDTO>>(){}.getType());
    }

    @Override
    public void updateUser(UserDTO userDTO, MultipartFile image) {
        User editUser = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("User ID: " + userDTO.getId()));

        if(passwordEncoder().matches(userDTO.getPassword(), editUser.getPassword())){

            if(!userDTO.getNewPassword().equals(""))
                editUser.setPassword(passwordEncoder().encode(userDTO.getNewPassword()));

            editUser.setBiography(userDTO.getBiography());
            editUser.setInterests(userDTO.getInterests());
            editUser.setCountry(userDTO.getCountry());
            editUser.setName(userDTO.getName());
            editUser.setSurname(userDTO.getSurname());
            editUser.setAvatar(userDTO.getAvatar());

            try{
                if(image != null){
                    Files.write(Path.of(DIRECTORY.formatted(userDTO.getId()) + "\\avatar.jpg"), image.getBytes());
                    editUser.setAvatar(DIRECTORY.formatted(userDTO.getId()) + "\\avatar.jpg");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            userRepository.save(editUser);
        } else throw new UserInvalidPasswordException("Invalid password!");


    }

    @Override
    public void deleteUser(int id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            assetRepository.deleteAllByUserId(id);
        }
        else throw new UserNotFoundException("Id: " + id);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException("Username: " + username));
    }

    @Override
    public void toggleUserStatus(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Id: " + id));
        if(!user.getRole().toString().equals("ADMIN"))
            user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    public void toggleUserSuspension(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Id: " + id));
        if(!user.getRole().toString().equals("ADMIN"))
            user.setSuspended(!user.isSuspended());
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> findAllEnabledUsers(UserSearchRequest userSearchRequest) {
        return modelMapper.map(userRepository.findAll(getSpecification(userSearchRequest, false)),
                new TypeToken<List<UserDTO>>(){}.getType());
    }

    private Specification<User> getSpecification(UserSearchRequest searchRequest, boolean findAll){
        return (root, cq, cb) -> {
            Predicate p = cb.conjunction();

            if(!findAll)
                p = cb.and(p, cb.equal(root.get("enabled"), true));

            if(searchRequest.getFilterText() != null){
                p = cb.and(p, cb.like(cb.lower(root.get("username")),"%" + searchRequest.getFilterText().toLowerCase() + "%"));
                p = cb.or(p, cb.like(cb.lower(root.get("name")), "%" + searchRequest.getFilterText().toLowerCase() + "%"));
                p = cb.or(p, cb.like(cb.lower(root.get("surname")), "%" + searchRequest.getFilterText().toLowerCase() + "%"));
                p = cb.or(p, cb.like(cb.lower(root.get("country")), "%" + searchRequest.getFilterText().toLowerCase() + "%"));
            }

            if(searchRequest.getSortDirection() == Sort.Direction.DESC)
                cq.orderBy(cb.desc(root.get(searchRequest.getSortBy())));
            else
                cq.orderBy(cb.asc(root.get(searchRequest.getSortBy())));
            return p;
        };
    }

}
