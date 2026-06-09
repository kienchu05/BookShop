package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.DTO.Request.CartRequestDTO;
import com.example.web_ban_sach.Service.IService.ICartItemService;
import io.jsonwebtoken.security.Request;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class CartController {
    private ICartItemService cartItemService;

    @PostMapping("/cart/add")
    public ResponseEntity<?> addCartItem(@RequestBody CartRequestDTO cartItem , Principal principal){
        return cartItemService.addCartItem(cartItem, principal);
    }

    @GetMapping("/cart/my-cart")
    public ResponseEntity<?> getMyCart(Principal principal){
        return cartItemService.getMyCart(principal);
    }
    
    @PutMapping("/cart/update/{cartItemId}")
    public ResponseEntity<?> updateCart(@PathVariable("cartItemId") Long cartItemId, Principal principal, @RequestParam int quantity){
        return cartItemService.updateCart(cartItemId, quantity, principal);
    }

    @DeleteMapping("/cart/remove/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){
        return cartItemService.deleteCartItem(cartItemId, principal);
    }

    @DeleteMapping("/cart/clear-cart")
    public ResponseEntity<?> clearCart(Principal principal){
        return cartItemService.clearCart(principal);
    }
}
