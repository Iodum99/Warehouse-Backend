package com.example.warehouse.service.implementation;

import com.example.warehouse.model.User;
import com.example.warehouse.security.authentication.AuthenticationRequest;
import com.example.warehouse.security.authentication.AuthenticationResponse;
import com.example.warehouse.security.util.JwtService;
import com.example.warehouse.service.AuthenticationService;
import com.example.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImplementation implements AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        User user = userService.findUserByUsername(authenticationRequest.getUsername());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
