package com.HerWell.example.Controller;

import com.HerWell.example.Data.User;
import com.HerWell.example.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AuthController {

    @Autowired
    private AuthService authService;

    // 1. Preflight handler for OPTIONS requests
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }

    // 2. Register Endpoint
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        String response = authService.register(user);
        return ResponseEntity.ok(response);
    }

    // 3. Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        String response = authService.login(email, password);
        return ResponseEntity.ok(response);
    }

    // 4. Profile Endpoint
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String email) {
        Optional<User> user = authService.getProfile(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
    }
}