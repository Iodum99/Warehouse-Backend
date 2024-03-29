package com.example.warehouse.controller;

import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.model.helper.UserSearchRequest;
import com.example.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/user")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody NewUserDTO userDTO) {
        userService.createUser(userDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @RequestPart("user") UserDTO userDTO,
            @RequestPart(value = "image", required = false) MultipartFile image){
        userService.updateUser(userDTO, image);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/status/{id}")
    public ResponseEntity<?> toggleUserSuspension(@PathVariable int id){
        userService.toggleUserSuspension(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id){
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<?> getAllUsers(
            @RequestParam("sortBy") String sortBy,
            @RequestParam("sortDirection") String sortDirection,
            @RequestParam(value = "filterByText", required = false) String text
    ){
        return new ResponseEntity<>(userService.findAllUsersAdmin(
                new UserSearchRequest(sortBy, sortDirection, text)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllEnabledUsers(
            @RequestParam("sortBy") String sortBy,
            @RequestParam("sortDirection") String sortDirection,
            @RequestParam(value = "filterByText", required = false) String text
    ){
        return new ResponseEntity<>(userService.findAllEnabledUsers(
                new UserSearchRequest(sortBy, sortDirection, text)), HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable int id){
       userService.deleteUser(id);
       return new ResponseEntity<>(HttpStatus.OK);
    }

}
