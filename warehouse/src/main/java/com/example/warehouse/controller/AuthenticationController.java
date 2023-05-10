package com.example.warehouse.controller;

import com.example.warehouse.security.authentication.AuthenticationRequest;
import com.example.warehouse.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
@CrossOrigin("http://localhost:4200")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest){
        return new ResponseEntity<>(authenticationService.login(authenticationRequest),HttpStatus.OK);
    }
}
