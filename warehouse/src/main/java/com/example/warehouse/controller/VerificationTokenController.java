package com.example.warehouse.controller;

import com.example.warehouse.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/token")
@CrossOrigin
@RequiredArgsConstructor
public class VerificationTokenController {

    private final VerificationTokenService verificationTokenService;

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verifyToken(@PathVariable String id){
        verificationTokenService.verifyToken(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
