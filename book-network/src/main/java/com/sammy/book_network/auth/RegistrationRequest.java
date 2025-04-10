package com.sammy.book_network.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "Firstname is mandatory") // field should not be empty
    @NotBlank(message = "Firstname is mandatory") // field should not have spaces
    private String firstname;
    @NotEmpty(message = "Lastname is mandatory")
    @NotBlank(message = "Lastname is mandatory")
    private String lastname;
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
