package com.example.warehouse.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/test")
@CrossOrigin
public class TestController {

    @GetMapping
    public ResponseEntity<?> test(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
