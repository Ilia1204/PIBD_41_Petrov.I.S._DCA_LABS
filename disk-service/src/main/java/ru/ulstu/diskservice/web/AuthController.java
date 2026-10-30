package ru.ulstu.diskservice.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.ulstu.diskservice.security.JwtService;
import ru.ulstu.diskservice.web.dto.LoginRequest;
import ru.ulstu.diskservice.web.dto.TokenResponse;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Выдача JWT-токена")
public class AuthController {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPasswordHash;

    public AuthController(JwtService jwtService, PasswordEncoder passwordEncoder,
            @Value("${app.security.admin-username}") String adminUsername,
            @Value("${app.security.admin-password-hash}") String adminPasswordHash) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPasswordHash = adminPasswordHash;
    }

    @PostMapping("/login")
    @Operation(summary = "Получить JWT-токен по логину и паролю")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        if (!adminUsername.equals(request.username()) || !passwordEncoder.matches(request.password(), adminPasswordHash)) {
            throw new BadCredentialsException("неверный логин или пароль");
        }
        return new TokenResponse(jwtService.generateToken(request.username()));
    }
}
