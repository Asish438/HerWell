package com.HerWell.example.Service;

import com.HerWell.example.Data.User;
import com.HerWell.example.Reposistry.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // Register user
    public String register(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists!";
        }
        userRepository.save(user);
        return "User registered successfully!";
    }

    // Login user
    public String login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                return "Login successful!";
            } else {
                return "Invalid password!";
            }
        } else {
            return "User not found!";
        }
    }

    // Get profile (extra)
    public Optional<User> getProfile(String email) {
        return userRepository.findByEmail(email);
    }
}
