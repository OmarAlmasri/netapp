package com.example.netapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.netapp.repository.UserRepository;

/*
 * this class is used for the UserDetailService inside the spring security authorization engine . 
 * so keep this as it is and don't change it at all please .
 */


@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}