package com.example.web_ban_sach.AuthService;

import com.example.web_ban_sach.DTO.Request.LoginRequest;
import com.example.web_ban_sach.DTO.Request.RegisterRequest;
import com.example.web_ban_sach.DTO.Response.AuthenticationResponse;
import com.example.web_ban_sach.DTO.Response.RegisterResponse;
import com.example.web_ban_sach.Entity.UserAccount;
import com.example.web_ban_sach.Repository.UserRepository;
import com.example.web_ban_sach.Service.IService.MailService;
import com.example.web_ban_sach.exception.Message;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {
    private static final int TOKEN_INDEX = 7;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private MailService  mailService;

    private String activatedCode(){
        return UUID.randomUUID().toString();
    }

    public void sendEmailtoActive(String email, String activatedCode){
        String subject = "Kích hoạt tài khoản của bạn tại BookStore  !";
        String text = "Vui lòng sử dụng mã kích hoạt sau cho tài khoản <"+email+"> : "+activatedCode+"  ";
        text += "</br> Click vào đường dẫn sau để kích hoạt tài khoản : ";
        String url = "http://localhost:3000/activate?activationCode=" + activatedCode;
        text += "<a>"+url+"</a>";
        mailService.sendMail("kienchu68@gmail.com", email, subject, text);
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ConstraintViolationException("User Existed !" , null);
        }
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConstraintViolationException("Email Existed !" , null);
        }
        //Gán và gửi thông tin kích hoạt mail

        UserAccount userAccount = UserAccount.builder()
                .username(registerRequest.getUsername())
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phone(registerRequest.getPhone())
                .activatedCode(activatedCode())
                .isActivated(false)
                .build();
        userRepository.save(userAccount);
            try {
               sendEmailtoActive(userAccount.getEmail(), userAccount.getActivatedCode());
            } catch (Exception e) {
                System.err.println("Lỗi gửi mail: " + e.getMessage());
            }
        return RegisterResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Register Successful")
                .build();
    }

    public ResponseEntity<?> activateAccount(String activationCode) {
        Optional<UserAccount> userAccount = userRepository.findByActivatedCode(activationCode);
        if (!userAccount.isPresent()) {
            return ResponseEntity.badRequest().body(new Message("Mã kích hoạt không hợp lệ hoặc đã hết hạn!"));
        }
        UserAccount user = userAccount.get();
        if (user.isActivated()) {
            return ResponseEntity.badRequest().body(new Message("Tài khoản đã được kích hoạt rồi!"));
        }
            user.setActivated(true);
            user.setActivatedCode("");
            userRepository.save(user);
         return ResponseEntity.badRequest().body(new Message("Kích hoạt tài khoản thành công !"));
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        log.info("User request body login : ");
        log.info("Login response body : {}", loginRequest);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        Optional<UserAccount> userAccount = userRepository.findByUsername(loginRequest.getUsername());
        if(userAccount.isPresent()){
            UserAccount user = userAccount.get();
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
            return AuthenticationResponse.builder()
                    .userId(user.getUserId())
                    .status(HttpStatus.OK.value())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .message("Login Successful !")
                    .role(user.getRoles().stream()
                            .map(role -> role.getName()) // Giả sử Role có hàm getName()
                            .collect(Collectors.joining(", "))) // Gộp thành chuỗi: "USER, ADMIN"
                    .build();
        }
        else{
            return AuthenticationResponse.builder()
                    .message("Login failed !")
                    .status(HttpStatus.FORBIDDEN.value())
                    .build();
        }
        }
}
