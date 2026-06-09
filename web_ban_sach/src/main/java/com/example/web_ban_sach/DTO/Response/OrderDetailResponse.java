package com.example.web_ban_sach.DTO.Response;
import lombok.Data;

@Data
public class OrderDetailResponse {
    private String bookName;
    private long quantity;
    private double price;
}