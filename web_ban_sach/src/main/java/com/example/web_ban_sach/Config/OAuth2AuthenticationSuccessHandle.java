package com.example.web_ban_sach.Config;

import com.example.web_ban_sach.AuthService.JwtService;
import com.example.web_ban_sach.Entity.Roles;
import com.example.web_ban_sach.Entity.UserAccount;
import com.example.web_ban_sach.Repository.RoleRepository;
import com.example.web_ban_sach.Repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandle extends SimpleUrlAuthenticationSuccessHandler
{
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        //1.Lấy thông tin User mà GG trả về cho Spring
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = defaultOAuth2User.getAttribute("email");
        String name =  defaultOAuth2User.getAttribute("name");

        //2. Tìm hoặc tạo mới User dưới database
        UserAccount userAccount = userRepository.findByEmail(email).orElseGet(()-> {
            UserAccount newUser = new UserAccount();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setUsername(email);
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

            Roles defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền USER trong database !"));
            List<Roles> roles = new ArrayList<>();
            roles.add(defaultRole);
            newUser.setRoles(roles
            );
            newUser.setIsActivated(true);


            return userRepository.save(newUser);
        });

        //3.Tạo ra chuỗi JWT mới (Do google trả về cho backend chứ không phải do backend tự tạo)
        String accessToken = jwtService.generateAccessToken(userAccount);
        String refreshToken = jwtService.generateRefreshToken(userAccount);

        userAccount.setAccessToken(accessToken);
        userAccount.setRefreshToken(refreshToken);
        userRepository.save(userAccount);

        //4.Kỹ thuật đưa tiền cho React: Redirect về React kèm Token trên thanh URL
        //React đang chờ ở đường dẫn: http://localhost:3000/oauth2/redirect
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("accessToken" , accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        // Ra lệnh cho trình duyệt nhảy về trang React
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
