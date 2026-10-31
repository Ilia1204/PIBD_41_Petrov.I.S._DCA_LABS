package ru.ulstu.diskservice.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.ulstu.diskservice.domain.User;
import ru.ulstu.diskservice.repository.UserRepository;
import ru.ulstu.diskservice.security.JwtService;
import ru.ulstu.diskservice.service.BusinessRuleException;
import ru.ulstu.diskservice.web.dto.LoginRequest;
import ru.ulstu.diskservice.web.dto.RegisterRequest;
import ru.ulstu.diskservice.web.dto.TokenResponse;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Регистрация и выдача JWT-токена")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(JwtService jwtService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @Operation(summary = "Зарегистрировать нового пользователя")
    public TokenResponse register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessRuleException("пользователь с логином " + request.username() + " уже существует");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        log.info("зарегистрирован новый пользователь {}", request.username());
        return new TokenResponse(jwtService.generateToken(request.username()));
    }

    @PostMapping("/login")
    @Operation(summary = "Получить JWT-токен по логину и паролю")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.username()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("неудачная попытка входа под логином {}", request.username());
            throw new BadCredentialsException("неверный логин или пароль");
        }
        log.info("выдан JWT-токен пользователю {}", request.username());
        return new TokenResponse(jwtService.generateToken(request.username()));
    }
}
