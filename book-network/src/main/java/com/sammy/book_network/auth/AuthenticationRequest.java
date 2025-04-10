package com.sammy.book_network.auth;


import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
    @Email(message = "Email is not formatted")
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should have a minimum of 8 characters!" ) // prevent user from entering less characters
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=.*[0-9])(?=.*[a-z])(?!.*(.).*\\1).*$",
            message = "Password must contain at least one uppercase letter, one special character, one number, one lowercase letter, and must not have duplicate characters!"
    ) // ensure that user enter a strong password
    private String password;
}


