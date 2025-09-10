package com.example.BookStore.service;

import com.example.BookStore.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getId(){
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // можно сделать логику из базы
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // можно сделать логику из базы
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // можно сделать логику из базы
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled(); // можно сделать логику из базы
    }
}
