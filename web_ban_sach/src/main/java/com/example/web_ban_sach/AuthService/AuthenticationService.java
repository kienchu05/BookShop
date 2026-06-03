package com.example.web_ban_sach.AuthService;

import com.example.web_ban_sach.DTO.Request.RegisterRequest;
import com.example.web_ban_sach.DTO.Response.RegisterResponse;
import com.example.web_ban_sach.Entity.UserAccount;
import com.example.web_ban_sach.Repository.UserRepository;
import com.example.web_ban_sach.exception.Message;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ConstraintViolationException("User Existed !" , null);
        }
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConstraintViolationException("Email Existed !" , null);
        }
        UserAccount userAccount = UserAccount.builder()
                .username(registerRequest.getUsername())
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phone(registerRequest.getPhone())
                .build();
        userRepository.save(userAccount);
        return RegisterResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Register Successful")
                .build();
    }
}
