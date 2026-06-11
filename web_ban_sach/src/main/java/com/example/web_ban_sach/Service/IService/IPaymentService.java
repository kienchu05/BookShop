package com.example.web_ban_sach.Service.IService;

import com.example.web_ban_sach.DTO.Request.CheckOutRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

public interface IPaymentService{
    ResponseEntity<?> checkout(CheckOutRequest request, Principal principal);
    public ResponseEntity<?> confirmPayment(HttpServletRequest request);
}
