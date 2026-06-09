package com.example.web_ban_sach.DTO.Request;
import lombok.Data;

@Data
public class CartRequestDTO {
    private Long bookId;
    private int quantity;
}