package com.example.web_ban_sach.DTO.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    @NotEmpty(message = "username is required !")
    private String username;

    @NotEmpty(message = "password is required !")
    private String password;
}
