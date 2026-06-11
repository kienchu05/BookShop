package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.DTO.Request.CheckOutRequest;
import com.example.web_ban_sach.Entity.Deliver;
import com.example.web_ban_sach.Entity.Payment;
import com.example.web_ban_sach.Repository.DeliverRepository;
import com.example.web_ban_sach.Repository.PaymentRepository;
import com.example.web_ban_sach.Service.ServiceImp.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class CheckOutController {
    private DeliverRepository deliverRepository;
    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    // Lấy danh sách Đơn vị vận chuyển
    @GetMapping("/checkout/deliveries")
    public ResponseEntity<List<Deliver>> getAllDeliveries() {
        List<Deliver> deliveries = deliverRepository.findAll();
        return ResponseEntity.ok(deliveries);
    }

    // Lấy danh sách Phương thức thanh toán
    @GetMapping("/checkout/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    @PostMapping("/order/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckOutRequest request, Principal principal) {
        return paymentService.checkout(request, principal);
    }

    @GetMapping("/checkout/vn-pay")
    public ResponseEntity<?> confirmPayment(HttpServletRequest request) {
        return paymentService.confirmPayment(request);
    }
}
