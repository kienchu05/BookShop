package com.example.web_ban_sach.Service.IService;

import com.example.web_ban_sach.Entity.UserAccount;
import org.springframework.http.ResponseEntity;

import java.util.Optional;


public interface IUserService {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    ResponseEntity<?> registerUser(UserAccount userAccount);
    ResponseEntity<?> deleteUser(Long id);
}
