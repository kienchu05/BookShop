package com.example.web_ban_sach.Service.ServiceImp;

import com.example.web_ban_sach.Entity.UserAccount;
import com.example.web_ban_sach.Repository.RoleRepository;
import com.example.web_ban_sach.Repository.UserRepository;
import com.example.web_ban_sach.Service.IService.IUserService;
import com.example.web_ban_sach.exception.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public ResponseEntity<?> registerUser(UserAccount userAccount) {
        if(userRepository.existsByUsername(userAccount.getUsername())) {
            return ResponseEntity.badRequest().body(new Message("Username Exsisted !"));
        }
        if(userRepository.existsByEmail(userAccount.getEmail())) {
            return ResponseEntity.badRequest().body(new Message("Email Exsisted !"));
        }
        UserAccount user = userRepository.save(userAccount);
        return ResponseEntity.ok("Đăng kí thành công !");
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Dùng Optional cho an toàn
        UserAccount user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        // Chặn user chưa kích hoạt
        if (!user.getIsActivated()) {
            throw new DisabledException("Tài khoản chưa được kích hoạt, vui lòng kiểm tra email!");
        }
        return user;
    }
}
