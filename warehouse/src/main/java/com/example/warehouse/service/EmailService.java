package com.example.warehouse.service;


public interface EmailService {

    void sendVerificationEmail(String email, String token);
}
