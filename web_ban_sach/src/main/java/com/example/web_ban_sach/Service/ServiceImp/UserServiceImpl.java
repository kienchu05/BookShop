package com.example.web_ban_sach.Service.ServiceImp;

import com.example.web_ban_sach.DTO.Request.UpdateRequest;
import com.example.web_ban_sach.Entity.UserAccount;
import com.example.web_ban_sach.Repository.RoleRepository;
import com.example.web_ban_sach.Repository.UserRepository;
import com.example.web_ban_sach.Service.IService.IUserService;
import com.example.web_ban_sach.exception.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
    public ResponseEntity<?> deleteUser(Long id) {
        Optional<UserAccount> userAccount = userRepository.findById(id);
        if(!userAccount.isPresent()) {
            return ResponseEntity.badRequest().body(new Message("UserAccount Not Found !"));
        }
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new Message("Xóa sách thành công!"));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message("Lỗi khi xóa sách: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> getMyProfile(Principal principal) {
        if(principal==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Principal Not Found !"));
        }
        String username = principal.getName();
        try{
            Optional<UserAccount> userAccount = userRepository.findByUsername(username);
            return   ResponseEntity.ok(userAccount);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.badRequest().body(new Message("Username Not Found !"));
        }
    }

    @Override
    public ResponseEntity<?> updateUser(UpdateRequest updateRequest , String username) {
        try {
            Optional<UserAccount> optional = userRepository.findByUsername(username);
            if(!optional.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new Message("UserAccount Not Found !"));
            }
            UserAccount user = optional.get();
            user.setName(updateRequest.getName());
            user.setPhone(updateRequest.getPhone());
            user.setGender(updateRequest.getGender());
            user.setAddress(updateRequest.getAddress());
            user.setDeliverAddress(updateRequest.getDeliverAddress());
            user.setPurchaseAddress(updateRequest.getPurchaseAddress());
            userRepository.save(user);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new Message("Cập nhật thông tin thành công !"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message("Lỗi khi thống khi lưu người dùng !"));
        }
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
