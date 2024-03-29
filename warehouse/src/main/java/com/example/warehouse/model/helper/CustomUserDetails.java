package com.example.warehouse.model.helper;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String role;
    private boolean enabled;
    private boolean suspended;

    public CustomUserDetails (String username, String password, String role, boolean enabled, boolean suspended){
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.suspended = suspended;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String authority;
        if(role.equals("USER"))
            authority = "ROLE_USER";
        else authority = "ROLE_ADMIN";

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);
        authorities.add(grantedAuthority);
        return authorities;
    }

    @Override
    public String getPassword() {
        return password ;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !suspended;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
