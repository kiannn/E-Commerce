package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    @Autowired
    UserRepository UserRepository;

    @Autowired
    BCryptPasswordEncoder BCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        final User findByUsername = UserRepository.findByUsername(authentication.getName());

        if (findByUsername == null || authentication.getCredentials() == null) {

            throw new BadCredentialsException("Failed to read credentials");
        }

        String password = authentication.getCredentials().toString();
        String username = authentication.getName();

        boolean matches = BCryptPasswordEncoder.matches(password, findByUsername.getPassword());

        if (!matches) {
            throw new BadCredentialsException("Mismatch credentials");
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(username,
                        password,
                        new ArrayList<>());

        return usernamePasswordAuthenticationToken;

    }

}
