package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.DTO.Request.UpdateRequest;
import com.example.web_ban_sach.Entity.UserAccount;
import com.example.web_ban_sach.Service.IService.IUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@Validated
public class UserController {
    private IUserService userService;

    @PostMapping("/user-account/register") // Api tự cấu hình sẽ phải cấp quyền riêng cho React
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserAccount userAccount) {
        return userService.registerUser(userAccount);
    }

    @DeleteMapping("/user-account/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/user/my-profile")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        return userService.getMyProfile(principal);
    }

    @PutMapping("/user/updateUser")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateRequest updateRequest,
                                        Principal principal) {
        if(principal==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập !");
        String username = principal.getName();
        return userService.updateUser(updateRequest, username);
    }


    @GetMapping("/test-hello")
    public String test() {
        return "HELLO KIEN, DUNG LA DU AN NAY ROI!";
    }
}
