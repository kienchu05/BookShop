package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.Service.IService.IOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class DashboardController {
    private IOrderService orderService;

    @GetMapping("/order/dashboard")
    public ResponseEntity<?> getDashboard(Principal principal) {
        return orderService.getDashboard(principal);
    }

}
