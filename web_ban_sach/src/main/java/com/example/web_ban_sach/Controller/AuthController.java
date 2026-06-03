package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.AuthService.AuthenticationService;
import com.example.web_ban_sach.DTO.Request.RegisterRequest;
import com.example.web_ban_sach.DTO.Response.RegisterResponse;
import com.example.web_ban_sach.Service.IUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class AuthController {
    private AuthenticationService authenticationService;
    private IUserService userService;

    @PostMapping("/user-account/registerUser")
    public ResponseEntity<RegisterResponse>  register(@Valid @RequestBody RegisterRequest registerRequest){
        RegisterResponse response = authenticationService.register(registerRequest);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping("/user/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/user/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
