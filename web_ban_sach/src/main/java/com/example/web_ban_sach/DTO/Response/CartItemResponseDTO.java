package com.example.web_ban_sach.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long bookId;
    private String bookName;
    private double priceFinal;
    private int quantity;
}