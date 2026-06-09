package com.example.web_ban_sach.Service.ServiceImp;

import com.example.web_ban_sach.DTO.Request.CartRequestDTO;
import com.example.web_ban_sach.DTO.Response.CartItemResponseDTO;
import com.example.web_ban_sach.Entity.Book;
import com.example.web_ban_sach.Entity.CartItem;
import com.example.web_ban_sach.Entity.UserAccount;
import com.example.web_ban_sach.Repository.BookRepository;
import com.example.web_ban_sach.Repository.CartItemRepository;
import com.example.web_ban_sach.Repository.UserRepository;
import com.example.web_ban_sach.Service.IService.ICartItemService;
import com.example.web_ban_sach.exception.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Data
public class CartItemServiceImpl implements ICartItemService {

    private CartItemRepository cartItemRepository;
    private UserRepository userRepository;
    private BookRepository bookRepository;

    @Override
    public ResponseEntity<?> addCartItem(CartRequestDTO cartItem ,  Principal principal) {
        if(principal == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Vui lòng đăng nhập!"));
        }
        UserAccount userAccount = userRepository.findByUsername(principal.getName()).orElseThrow();
        Book book = bookRepository.findById(cartItem.getBookId()).orElseThrow();

        if(book.getQuantity() < cartItem.getQuantity()){
            return ResponseEntity.badRequest().body(new Message("Số lượng tồn kho không đủ !"));
        }
        Optional<CartItem> existingItem = cartItemRepository.findByUserAccountAndBook(userAccount,book);
        //neu gio hang co ton tai
        if(existingItem.isPresent()){
            CartItem existingCartItem = existingItem.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItem.getQuantity());
            cartItemRepository.save(existingCartItem);
        }else{
            CartItem cart = new CartItem();
            cart.setUserAccount(userAccount);
            cart.setBook(book);
            cart.setQuantity(cartItem.getQuantity());
            cartItemRepository.save(cart);
        }
        return ResponseEntity
                .ok()
                .body(new Message("Thêm vào giỏ hàng thành công !"));
    }

    //Xem gio hang
    @Override
    public ResponseEntity<?> getMyCart(Principal principal) {
        if(principal == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Vui lòng đăng nhập !"));
        }
        UserAccount userAccount = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findByUserAccount(userAccount);

        List<CartItemResponseDTO> response = cartItems.stream().map(item -> new CartItemResponseDTO(
                item.getId(),
                item.getBook().getId(),
                item.getBook().getName(),
                item.getBook().getPriceFinal(),
                item.getQuantity()
        )).collect(Collectors.toList());
        return ResponseEntity
                .ok()
                .body(response);
    }

    @Override
    public ResponseEntity<?> updateCart(Long cartItemId, int quantity, Principal principal) {
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow();

        // Bảo mật: Kiểm tra xem món đồ này có đúng là của user đang đăng nhập không
        if (!item.getUserAccount().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền chỉnh sửa!");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return ResponseEntity.ok("Đã xóa khỏi giỏ hàng");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return ResponseEntity.ok("Cập nhật số lượng thành công");
    }

    @Override
    public ResponseEntity<?> deleteCartItem(Long cartItemId, Principal principal) {
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow();

        if (!item.getUserAccount().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền xóa!");
        }

        cartItemRepository.delete(item);
        return ResponseEntity.ok("Đã xóa món hàng!");
    }
    }
