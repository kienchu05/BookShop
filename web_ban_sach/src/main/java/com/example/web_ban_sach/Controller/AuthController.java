package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.AuthService.AuthenticationService;
import com.example.web_ban_sach.DTO.Request.LoginRequest;
import com.example.web_ban_sach.DTO.Request.RegisterRequest;
import com.example.web_ban_sach.DTO.Response.AuthenticationResponse;
import com.example.web_ban_sach.DTO.Response.RegisterResponse;
import com.example.web_ban_sach.Service.IService.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
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

    @GetMapping("/user/activate-account")
    public ResponseEntity<?> activateAccount(@RequestParam("activationCode") String activationCode){
        return authenticationService.activateAccount(activationCode);
    }

    @PostMapping("/user/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        AuthenticationResponse response = authenticationService.login(loginRequest);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PostMapping("/user/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) throws ValidationException {
        return authenticationService.refreshToken(authHeader);
    }
}
