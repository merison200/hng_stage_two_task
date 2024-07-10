package com.emma.HNG2.controller;

import com.emma.HNG2.model.User;
import com.emma.HNG2.service.UserService;
import com.emma.HNG2.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            String token = jwtUtil.generateToken(registeredUser.getEmail());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "success");
            response.put("message", "Registration successful");

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("accessToken", token);
            data.put("user", registeredUser);
            response.put("data", data);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "Bad request");
            response.put("message", e.getMessage());
            response.put("statusCode", 400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "Internal Server Error");
            response.put("message", "An unexpected error occurred");
            response.put("statusCode", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        return userService.getUserByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getEmail());
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("status", "success");
                    response.put("message", "Login successful");

                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("accessToken", token);
                    data.put("user", user);

                    response.put("data", data);

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("status", "Bad request");
                    response.put("message", "Authentication failed");
                    response.put("statusCode", 401);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                });
    }
}