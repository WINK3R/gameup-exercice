package com.gamesUP.gamesUP.controller;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamesUP.gamesUP.dto.AuthRequest;
import com.gamesUP.gamesUP.dto.AuthResponse;
import com.gamesUP.gamesUP.dto.RegisterRequest;
import com.gamesUP.gamesUP.model.User;
import com.gamesUP.gamesUP.service.UserService;
import com.gamesUP.gamesUP.security.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setDisplayName(request.displayName());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(User.Role.CLIENT);
        User saved = userService.create(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                saved.getEmail(),
                saved.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + saved.getRole().name()))
        );
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }
}
