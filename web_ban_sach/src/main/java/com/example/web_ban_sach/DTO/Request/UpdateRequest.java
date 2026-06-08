package com.example.web_ban_sach.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateRequest {
    @NotEmpty(message = "Name must not be empty !")
    private String name;

    @NotNull(message = "Phone must not be null")
    private String phone;

    private String gender;
    private String address;

    private String deliverAddress;

    private String purchaseAddress;
}
