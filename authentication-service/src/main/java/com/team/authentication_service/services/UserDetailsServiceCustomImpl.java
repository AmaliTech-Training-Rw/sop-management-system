package com.team.authentication_service.services;

import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsServiceCustomImpl implements UserDetailsService {
    UserRepository userRepository;

    public UserDetailsServiceCustomImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return new org.springframework.security.core.userdetails
                .User(user.get().getEmail(), user.get().getPassword(), user.orElseThrow().getAuthorities());
    }
}
