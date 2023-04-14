package com.example.warehouse.service;

import com.example.warehouse.security.authentication.AuthenticationRequest;
import com.example.warehouse.security.authentication.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest authenticationRequest);
}
