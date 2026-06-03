package com.example.web_ban_sach.Service;

import com.example.web_ban_sach.Entity.UserAccount;
import com.example.web_ban_sach.Repository.RoleRepository;
import com.example.web_ban_sach.Repository.UserRepository;
import com.example.web_ban_sach.exception.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    @Override
    public UserAccount findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserAccount findByEmail(String email) {
        return userRepository.findByEmail(email);
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

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }
        List<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map(
                role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase())
        ).collect(Collectors.toList());

        return new User(user.getUsername(), user.getPassword(), grantedAuthorities);

    }
}
