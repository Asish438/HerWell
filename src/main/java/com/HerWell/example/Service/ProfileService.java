package com.HerWell.example.Service;

import com.HerWell.example.Data.User;
import com.HerWell.example.Reposistry.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    // Fetch profile by email
    public Optional<User> getProfileByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Update profile
    public User updateProfile(String email, User updatedUser) {
        return userRepository.findByEmail(email).map(user -> {
            if (updatedUser.getFullName() != null) user.setFullName(updatedUser.getFullName());
            if (updatedUser.getAge() != null) user.setAge(updatedUser.getAge());
            if (updatedUser.getHeight() != null) user.setHeight(updatedUser.getHeight());
            if (updatedUser.getWeight() != null) user.setWeight(updatedUser.getWeight());
            if (updatedUser.getBloodGroup() != null) user.setBloodGroup(updatedUser.getBloodGroup());
            if (updatedUser.getSugarPatient() != null) user.setSugarPatient(updatedUser.getSugarPatient());
            if (updatedUser.getBloodPressurePatient() != null) user.setBloodPressurePatient(updatedUser.getBloodPressurePatient());
            if (updatedUser.getGastricIssue() != null) user.setGastricIssue(updatedUser.getGastricIssue());

            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}