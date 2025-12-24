package com.example.chatu.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    String username;

    @NotBlank
    @Email
    String email;

    @NotBlank
    String password;

}
