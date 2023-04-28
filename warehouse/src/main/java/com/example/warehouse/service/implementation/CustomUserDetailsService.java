package com.example.warehouse.service.implementation;

import com.example.warehouse.model.User;
import com.example.warehouse.model.helper.CustomUserDetails;
import com.example.warehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameContainingIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username));
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getRole().toString(),
                user.isEnabled());
    }
}

