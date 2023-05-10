package com.example.warehouse.service.implementation;

import com.example.warehouse.exception.VerificationTokenExpiredException;
import com.example.warehouse.exception.VerificationTokenNotFoundException;
import com.example.warehouse.model.VerificationToken;
import com.example.warehouse.repository.VerificationTokenRepository;
import com.example.warehouse.service.UserService;
import com.example.warehouse.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImplementation implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserService userService;


    @Override
    public void verifyToken(String tokenId) {
        VerificationToken token = verificationTokenRepository.findById(UUID.fromString(tokenId))
                .orElseThrow(()-> new VerificationTokenNotFoundException("TokenId: " + tokenId));
        if(LocalDateTime.now().isAfter(token.getExpiresAt())){
            verificationTokenRepository.delete(token);
            throw new VerificationTokenExpiredException("TokenId: " + tokenId);
        }
        else {
            userService.toggleUserStatus(token.getUserId());
            verificationTokenRepository.delete(token);
        }
    }
}
