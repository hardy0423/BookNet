package com.ydrah.book.service;

import com.ydrah.book.Enum.RoleEnum;
import com.ydrah.book.apiResponse.ApiResponse;
import com.ydrah.book.dto.AuthenticationRequest;
import com.ydrah.book.dto.RegisterUserDto;
import com.ydrah.book.email.EmailService;
import com.ydrah.book.email.EmailTemplateName;
import com.ydrah.book.exception.ActivationTokenException;
import com.ydrah.book.repositories.RoleRepository;
import com.ydrah.book.repositories.TokenRepository;
import com.ydrah.book.repositories.UserRepository;
import com.ydrah.book.role.Role;
import com.ydrah.book.user.Token;
import com.ydrah.book.user.User;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    @Value("${application.mailing.frontend.activation-link}")
    private String activationUrl;

    public AuthenticationService(UserRepository userRepository, JwtService jwtService,
                                 RoleRepository roleRepository, PasswordEncoder passwordEncoder,

                                 AuthenticationManager authenticationManager, EmailService emailService, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
    }

    public ApiResponse<Object> register(@Valid RegisterUserDto registerUserDto) throws MessagingException {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .dateOfBirth(registerUserDto.getDateOfBirth())
                .email(registerUserDto.getEmail())
                .password(passwordEncoder.encode(registerUserDto.getPassword()))
                .roles(Set.of(userRole))
                .accountLocked(false)
                .accountEnabled(true)
                .build();
        userRepository.save(user);

        sendValidationEmail(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefresh(new HashMap<>(), user);

        return ApiResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

    }

    public ApiResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                authenticationRequest.getPassword()));

        var user = this.userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        String roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefresh(new HashMap<>(), user);
        return ApiResponse.builder().statusCode(HttpStatus.OK.value())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .role(roles)
                .userId(user.getId())
                .build();
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDate.from(LocalDateTime.now()))
                .expiresAt(LocalDate.from(LocalDateTime.now().plusMinutes(15)))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    //    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ActivationTokenException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt().atTime(23, 59, 59))) {
            sendValidationEmail(savedToken.getUser());
            throw new ActivationTokenException("Activation token has expired. A new token has been sent to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setAccountEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDate.from(LocalDateTime.now()));
        tokenRepository.save(savedToken);
    }



}
