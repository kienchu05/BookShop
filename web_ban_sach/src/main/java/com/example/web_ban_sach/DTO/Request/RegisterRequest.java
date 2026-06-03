package com.example.web_ban_sach.DTO.Request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterRequest {
    @NotEmpty(message = "Name must not be empty !")
    private String name;

    @NotEmpty(message = "username must not be empty !")
    private String username;

    @NotNull(message = "Email must not be null !")
    @Email(message = "Malformed email !")
    private String email;

    @NotNull(message = "Password must not be null !")
    private String password;

    @NotNull(message = "Phone must not be null")
    private String phone;

    @NotNull(message =  "Role must not be null !")
    @Pattern(regexp = "ADMIN|MANAGER|USER", message = "The role must be ADMIN , MANAGER or USER !")
    private String role;
}
