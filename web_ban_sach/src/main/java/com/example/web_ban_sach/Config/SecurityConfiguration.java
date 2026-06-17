package com.example.web_ban_sach.Config;

import com.example.web_ban_sach.JwtFilter.JwtAuthenticationFilter;
import com.example.web_ban_sach.Repository.UserRepository;
import com.example.web_ban_sach.Service.ServiceImp.UserServiceImpl;
import com.example.web_ban_sach.exception.ErrorHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@Slf4j
@AllArgsConstructor
public class SecurityConfiguration {
    private final UserRepository userRepository;
    private final JwtAuthenticationFilter  jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http , ErrorHandler errorHandler) throws Exception {
        http
//                .httpBasic(Customizer.withDefaults())
                .csrf((customizer) -> customizer.disable())
//                .anonymous(anonymous -> anonymous.disable())
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
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // giải mã token để lấy quyền
                .authorizeHttpRequests(customize ->
                        customize
                                // Thêm đường dẫn chính xác của API kiểm tra
                                .requestMatchers(HttpMethod.GET, "/user/activate-account").permitAll()
                                .requestMatchers(HttpMethod.POST, "/user-account/registerUser").permitAll()
                                .requestMatchers(HttpMethod.POST, "/user/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user/check-username").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user/check-email").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user-account/search/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/user-account/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/order/dashboard").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/book/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/category/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/book/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/book/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/book/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/book/add-book").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/book/update-book").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/user/my-profile").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/user/updateUser").authenticated()

                                .requestMatchers(HttpMethod.POST, "/cart/add").authenticated()
                                .requestMatchers(HttpMethod.GET, "/cart/my-cart").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/cart/update/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/cart/remove/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/cart/clear-cart").authenticated()

                                .requestMatchers(HttpMethod.GET, "/checkout/deliveries").authenticated()
                                .requestMatchers(HttpMethod.GET, "/checkout/payments").authenticated()
                                .requestMatchers(HttpMethod.POST, "/order/checkout").authenticated()
                                .requestMatchers(HttpMethod.GET, "/order/my-order").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/order/delete-order/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/checkout/vn-pay").permitAll()

                                .requestMatchers(HttpMethod.GET, "/image/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/user/refresh-token").permitAll()

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

    @Bean
    //kiểm tra thông tin đăng nhập, để có thể đem ra sử dụng ở các chỗ khác (ví dụ như trong API Đăng nhập).
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        log.info("Create bean Authentication *********");
        return authenticationConfiguration.getAuthenticationManager();//so sánh mật khẩu khách đưa với mật khẩu trong hồ sơ xem có khớp không. Khớp thì cấp quyền
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

//    @Bean
//    //Mặc định, Spring Security không biết bạn xài MySQL, Oracle, hay MongoDB.
//    // Nó bắt bạn phải cung cấp một cái máy dò (UserDetailsService).
//    public UserDetailsService userDetailsService(){
//        return ((username) ->
//                userRepository.findByUsername(username) //Nếu tìm thấy thì trả về thông tin (hồ sơ, mật khẩu đã mã hóa, quyền hạn)
//                        .orElseThrow(() -> new UsernameNotFoundException("username not found!")));//Nếu không thấy sẽ ném lỗi
//    }
}
