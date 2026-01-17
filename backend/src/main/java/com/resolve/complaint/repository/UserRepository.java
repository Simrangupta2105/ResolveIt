package com.resolve.complaint.repository;

import com.resolve.complaint.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    
    // Additional methods for admin functionality
    List<User> findByRole(User.Role role);
    List<User> findByRoleIn(List<User.Role> roles);
    long countByRole(User.Role role);
}