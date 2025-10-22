package com.example.IMS_Backend.service;

import com.example.IMS_Backend.model.User;
import com.example.IMS_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        System.out.println("Loading user: " + username + " with roles: " +
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("NO ROLES"));

        return UserDetailsImpl.build(user);
    }
}