package com.example.web_ban_sach.Service.IService;

import com.example.web_ban_sach.DTO.Request.CartRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

public interface ICartItemService {
    ResponseEntity<?> addCartItem(@RequestBody CartRequestDTO cartItem,  Principal principal);
    ResponseEntity<?> getMyCart(Principal principal);
    ResponseEntity<?> deleteCartItem(Long cartItemId, Principal principal);
    ResponseEntity<?> updateCart(Long cartItemId, int quantity, Principal principal);
    ResponseEntity<?> clearCart(Principal principal);
}
