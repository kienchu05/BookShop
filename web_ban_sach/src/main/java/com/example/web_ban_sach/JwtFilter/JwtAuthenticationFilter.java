package com.example.web_ban_sach.JwtFilter;

import com.example.web_ban_sach.AuthService.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Getter
@Setter
@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1.Lay ra header
        final String authorizationHeader = request.getHeader("Authorization");

        //2.Kiem tra xem header co  chua chuoi "Bearer " hay khoong
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //3.Cat chuoi de lay token
        final String token = authorizationHeader.substring(7);

        //4.Tu token lay ra user
        final String username = jwtService.extractUsername(token);

        // 5. Dòng này để tránh việc xác thực lại một người đã được xác thực rồi
        //(Tức là nhằm việc xác thực một user chưa từng đc xác thức một lần nào)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //6.Kiểm tra xem token còn hạn và có khớp với user không
            if(jwtService.isTokenValid(token, userDetails)) {
                // 7. Tạo đối tượng Authentication chứa quyền (Roles) của user (Thẻ thông hành)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, //Chỗ này đáng lẽ ghi Mật khẩu, nhưng vì đã chứng minh bằng JWT rồi nên không cần lộ mật khẩu ra đây nữa.
                        userDetails.getAuthorities()
                );
                // Lưu thêm thông tin chi tiết của request (ví dụ: IP address, session ID)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                // 8. Cập nhật SecurityContext -> Chính thức xác nhận là user đã đăng nhập!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            };
        }
        // 9. Cho phép request đi tiếp tới các Filter khác hoặc Controller
        filterChain.doFilter(request, response);
    }
}
