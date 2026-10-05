package com.aurahealth.hms.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Real credential-backed login. Looks the email up in the {@code users}
 * table seeded by V4__seed_users.sql, verifies the password against its
 * BCrypt hash, and issues a JWT carrying the role from the database record
 * — never a client-supplied role.
 */
@RestController
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record LoginResponse(String token, String name, String role, String email) {}

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Incorrect email or password.");
        }
    }

    @PostMapping("/api/v1/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.issueToken(user.getName(), user.getRole());
        return new LoginResponse(token, user.getName(), user.getRole().name(), user.getEmail());
    }
}
