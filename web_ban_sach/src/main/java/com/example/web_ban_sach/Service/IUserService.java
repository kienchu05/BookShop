package com.example.web_ban_sach.Service;

import com.example.web_ban_sach.Entity.UserAccount;
import org.springframework.http.ResponseEntity;


public interface IUserService {
    UserAccount findByUsername(String username);
    UserAccount findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    ResponseEntity<?> registerUser(UserAccount userAccount);
}
