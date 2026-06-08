package com.example.web_ban_sach.AuthService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {
    @Value("${app.auth.tokenSecret}")
    private String secretKey;

    @Value("${app.auth.tokenExpiration}")
    private long expiration;

    @Value("${app.auth.tokenRefreshExpiration}")
    private long refreshExpiration;

    //Tạo mới token thì cần có đầy đủ thông tin user từ ban đầu
    //Đây là hàm bên dưới
    public String generateAccessToken(Map<String, Object> claims , UserDetails userDetails) {
        return buildToken(claims ,userDetails, expiration);
    }

    // làm mới token thì chỉ cần xác minh user đó là ai (giống việc làm lại cccd)
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>() ,  userDetails, refreshExpiration);
    }

    // Nếu ở một nơi nào đó trong code,
    // bạn chỉ muốn tạo token cơ bản mà không có thông tin tùy chỉnh nào (tức là claims rỗng),
    // bạn sẽ phải gọi hàm rất dài dòng: generateAccessToken(new HashMap<>(), userDetails)
    //Khi bạn gọi hàm này, nó không tự tạo token.
    // Nó sẽ tự động tạo ra một cái giỏ rỗng (new HashMap<>()) đại diện cho claims rỗng,
    // rồi truyền thẳng cái giỏ rỗng đó cùng với userDetails xuống cho "Hàm bên dưới" xử lý tiếp.
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(),  userDetails);
    }

    public String buildToken(Map<String, Object> claims , UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(secretKey),SignatureAlgorithm.HS256)
                .compact();
    }

    public SecretKey  getSecretKey(String secretKey) {
        byte[] bytesKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(bytesKey);//sẽ nhận vào mảng byte thô (vừa được giải mã từ chuỗi Base64 siêu an toàn)
        //"nâng cấp" nó thành một đối tượng SecretKey đạt chuẩn mật mã học
        //Keys.hmacShaKeyFor(bytesKey); không phải hàm băm của secretKey
    }

    public String formHex(String value){
        return new String(HexFormat.of().parseHex(value));
    }

    public Claims extractAllClaims(String token){
        //Một claims là 1 thông tin của người dùng và lưu trong phần payload của token
        //Khi giải mã được token thì sẽ trả về một kiểu dữ liệu Key-Value được thể hiện dưới dạng JSON
        // ví dụ :{
        //  "sub": "namkute123",
        //  "iat": 1715000000,
        //  "exp": 1715003600,
        //  "role": "ADMIN",
        //  "user_id": 1024,
        //  "department": "IT"
        //}
        return Jwts.parser()
                .verifyWith(getSecretKey(secretKey))    //Hãy dùng chiếc chìa khóa này để đối chiếu chữ ký (Signature) của token sắp đưa vào.
                // Nếu chữ ký không được tạo ra từ chìa khóa này, tuyệt đối không cho qua!
                .build()
                .parseSignedClaims(token) //Nó tự động lấy Header và Payload của token, dùng Secret Key băm lại thử.
                // Nếu chuỗi băm ra giống hệt với phần Signature thứ 3 của token, nó mới tin token này là đồ thật, không bị ai sửa đổi giữa đường
                .getPayload();
    }

    //function<Claims ,T> : Nhận vào kiểu dữ liệu là Claims(như là cái hộp) và trả về bất kì giá trị nào (Generics)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //Hàm này cần lấy ra Username
    //Username thường được cất trong trường Subject của Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //nếu muốn lấy ra date thì
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //Kiem tra xem token co xac thuc duoc nguoi dung hay khong
    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        String username = extractUsername(jwt);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
    }

    //Kiem tra token con han hay khong
    private boolean isTokenExpired(String jwt) {
        Date expiration = this.extractExpiration(jwt);
        return expiration.before(new Date());
        //Giả sử hiện tại đang là 10:00 sáng (new Date() đại diện bằng số 1000).
        //Token đến tận 11:00 trưa mới hết hạn. Suy ra biến expiration mang giá trị là 1100.
        //Kết quả: Sai (False). Nó xảy ra sau hiện tại. Hàm trả về false $\rightarrow$ Token vẫn hợp lệ, cho đi tiếp!
    }
}
