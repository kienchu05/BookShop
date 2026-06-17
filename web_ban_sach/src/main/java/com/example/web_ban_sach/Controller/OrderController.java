package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.DTO.Request.CheckOutRequest;
import com.example.web_ban_sach.Service.IService.IOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class OrderController {
    private IOrderService orderService;

    @GetMapping("/order/my-order")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        return orderService.getMyOrders(principal);
    }

    @DeleteMapping("/order/delete-order/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Long id,  Principal principal) {
        return orderService.deleteOrder(id, principal);
    }

}
