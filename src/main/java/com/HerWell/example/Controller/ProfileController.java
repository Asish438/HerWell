package com.HerWell.example.Controller;

import com.HerWell.example.Data.User;
import com.HerWell.example.Service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*") // ✅ Crucial for Frontend-Backend connection
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // ✅ Get profile by email -> URL: GET http://localhost:7377/api/profile/{email}
    @GetMapping("/{email}")
    public ResponseEntity<?> getProfile(@PathVariable String email) {
        Optional<User> user = profileService.getProfileByEmail(email);
        return user.isPresent() ? ResponseEntity.ok(user.get()) : ResponseEntity.notFound().build();
    }

    // ✅ Update profile -> URL: PUT http://localhost:7377/api/profile/{email}
    @PutMapping("/{email}")
    public ResponseEntity<?> updateProfile(@PathVariable String email, @RequestBody User updatedUser) {
        try {
            User savedUser = profileService.updateProfile(email, updatedUser);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}