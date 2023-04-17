package com.example.warehouse.service;


import jakarta.mail.MessagingException;

public interface EmailService {

    void sendVerificationEmail(String email, String token);
}
