package com.resolve.complaint.service;

import com.resolve.complaint.model.User;
import com.resolve.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> getAdminUsers() {
        return userRepository.findByRole(User.Role.ADMIN);
    }

    public List<User> getEmployeeUsers() {
        return userRepository.findByRole(User.Role.EMPLOYEE);
    }

    public long getTotalUserCount() {
        return userRepository.count();
    }

    public long getUserCountByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    public List<User> getAssignableUsers() {
        // Get all staff members (excluding regular users)
        List<User> allStaff = userRepository.findByRoleIn(
            List.of(User.Role.ADMIN, User.Role.EMPLOYEE, User.Role.MANAGER, User.Role.SUPERVISOR)
        );
        
        // Filter out restricted users by name/fullName
        return allStaff.stream()
            .filter(user -> !isRestrictedUser(user))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private boolean isRestrictedUser(User user) {
        String fullName = user.getFullName() != null ? user.getFullName().toLowerCase() : "";
        String username = user.getUsername() != null ? user.getUsername().toLowerCase() : "";
        
        // List of restricted names/titles
        String[] restrictedNames = {
            "simran gupta",
            "anonymous",
            "department manager", 
            "team supervisor",
            "system administrator"
        };
        
        for (String restrictedName : restrictedNames) {
            if (fullName.contains(restrictedName) || username.contains(restrictedName)) {
                return true;
            }
        }
        
        return false;
    }
}