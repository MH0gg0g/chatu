package com.example.chatu.secuirty;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatu.user.LoginRequest;
import com.example.chatu.user.RegisterRequest;
import com.example.chatu.user.ResponseDTO;
import com.example.chatu.user.User;
import com.example.chatu.user.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterRequest req) {
        try {
            User u = userService.register(req.getUsername(), req.getEmail(), req.getPassword());
            return ResponseEntity.ok(new ResponseDTO(u.getUsername() + " registered successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ResponseDTO(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

            String token = jwtService.generateToken(auth.getName());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}
