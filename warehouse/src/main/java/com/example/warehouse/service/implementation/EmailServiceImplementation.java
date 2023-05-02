package com.example.warehouse.service.implementation;

import com.example.warehouse.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}") private String sender;
    @Override
    public void sendVerificationEmail(String email, String token){

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);
            helper.setSubject("Account Verification");
            helper.setFrom(sender);
            mimeMessage.setContent("http://localhost:4200/verification/token/" + token, "text/html");
            javaMailSender.send(mimeMessage);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
