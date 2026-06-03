package com.example.web_ban_sach.Config;

import com.example.web_ban_sach.Service.UserServiceImpl;
import com.example.web_ban_sach.exception.ErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http , ErrorHandler errorHandler) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .csrf((customizer) -> customizer.disable())
                //@CrossOrigin : Cho phép trình duyệt (từ domain này) gọi API đến server (domain khác).
                .cors(cors -> {
                    cors.configurationSource(request -> {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
                        corsConfiguration.addAllowedOrigin("http://localhost:3000");
                        corsConfiguration.addAllowedHeader("*");
                        return corsConfiguration;
                    });
                })
                //không lưu lại thông tin đăng nhập vào bộ nhớ (Session).
                .sessionManagement(
                        (customizer) -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler))
                .authorizeHttpRequests(customize ->
                customize
                        // Thêm đường dẫn chính xác của API kiểm tra
                        .requestMatchers(HttpMethod.POST, "/user-account/registerUser").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/check-username").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/check-email").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user-account/search/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/book/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/image/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user-account").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/user-account/register").permitAll()
                );
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserServiceImpl userServiceImpl) {
        DaoAuthenticationProvider dap = new DaoAuthenticationProvider(userServiceImpl);
        dap.setPasswordEncoder(bCryptPasswordEncoder());
        return dap;
    }
}
