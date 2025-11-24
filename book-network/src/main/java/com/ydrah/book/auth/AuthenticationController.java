package com.ydrah.book.auth;

import com.ydrah.book.apiResponse.ApiResponse;
import com.ydrah.book.dto.AuthenticationRequest;
import com.ydrah.book.dto.RegisterUserDto;
import com.ydrah.book.exception.UserAlreadyExistsException;
import com.ydrah.book.repositories.UserRepository;
import com.ydrah.book.service.AuthenticationService;
import com.ydrah.book.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "APIs for user authentication and registration")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid  @RequestBody RegisterUserDto dto) throws MessagingException {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        authenticationService.register(dto);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("User registered successfully!")
                .statusCode(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        System.out.println("Authentication request received for email: " + authenticationRequest.getEmail());
        ApiResponse res = authenticationService.authenticate(authenticationRequest);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/activate")
    public ResponseEntity<ApiResponse> activateAccount(@RequestParam("token") String token) throws MessagingException {
        authenticationService.activateAccount(token);

        ApiResponse response = ApiResponse.builder()
                .message("Account activated successfully!")
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
