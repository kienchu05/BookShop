package com.example.web_ban_sach.DTO.Request;
import lombok.Data;

@Data
public class CheckOutRequest {
    private String purchaseAddress;
    private String deliverAddress;
    private Long paymentId;
    private Long deliverId;
}