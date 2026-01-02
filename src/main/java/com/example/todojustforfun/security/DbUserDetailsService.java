package com.example.todojustforfun.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface DbUserDetailsService {
    UserDetails loadUserByEmail(String email);
}
