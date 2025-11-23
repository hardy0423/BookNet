package com.ydrah.book.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserDto {
    @NotEmpty(message="First name must not be empty")
    @NotBlank(message="First name must not be blank")
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    @Email(message = "Email should be valid")
    private String email;
    @NotEmpty(message = "Password must not be empty")
    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
