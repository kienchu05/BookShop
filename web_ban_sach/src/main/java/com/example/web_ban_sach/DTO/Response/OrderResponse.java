package com.example.web_ban_sach.DTO.Response;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private LocalDateTime creationDate;
    private String deliverAddress;
    private double totalPrice;
    private double shippingPrice;
    private String paymentMethod;
    private String deliverMethod;
    private List<OrderDetailResponse> orderDetails; 
}
