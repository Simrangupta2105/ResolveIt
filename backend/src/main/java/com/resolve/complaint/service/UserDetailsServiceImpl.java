package com.resolve.complaint.service;

import com.resolve.complaint.model.User;
import com.resolve.complaint.repository.UserRepository;
import com.resolve.complaint.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Try to find user by username first
        Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);
        
        // If not found by username, try to find by email
        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByEmail(usernameOrEmail);
        }
        
        // If still not found, throw exception
        User user = userOptional.orElseThrow(() -> 
            new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

        return UserPrincipal.create(user);
    }
}