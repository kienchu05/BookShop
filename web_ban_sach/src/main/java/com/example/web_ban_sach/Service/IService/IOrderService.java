package com.example.web_ban_sach.Service.IService;

import com.example.web_ban_sach.DTO.Request.CheckOutRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

public interface IOrderService {
    ResponseEntity<?> getMyOrders(Principal principal);
    ResponseEntity<?> deleteOrder(Long id ,Principal principal);
}
