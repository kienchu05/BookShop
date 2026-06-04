package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.Entity.UserAccount;
import com.example.web_ban_sach.Service.IService.IUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/test-hello")
    public String test() {
        return "HELLO KIEN, DUNG LA DU AN NAY ROI!";
    }
}
