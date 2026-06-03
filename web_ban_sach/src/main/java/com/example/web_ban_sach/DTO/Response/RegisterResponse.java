package com.example.web_ban_sach.DTO.Response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private int status;
    private String message;
}
