package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.Entity.Deliver;
import com.example.web_ban_sach.Entity.Payment;
import com.example.web_ban_sach.Repository.DeliverRepository;
import com.example.web_ban_sach.Repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class CheckOutController {
    private DeliverRepository deliverRepository;
    private PaymentRepository paymentRepository;

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
}
