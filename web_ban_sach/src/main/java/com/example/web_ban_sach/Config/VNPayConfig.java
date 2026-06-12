package com.example.web_ban_sach.Config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Configuration
public class VNPayConfig {
    public static String hashAllFields(Map<String , String> fields, String  secretKey){
        List<String> fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuffer sb = new StringBuffer();
        for(String fieldName : fieldNames){
            String fieldValue = fields.get(fieldName);
            if(fieldValue != null && fieldValue.length() > 0){
                if(sb.length() > 0){
                    sb.append('&');
                }

                //Noi ten bien va gia tri da ma hoa
                sb.append(fieldName).append('=').append(java.net.URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            }
        }
        return hmacSHA512(secretKey, sb.toString());
    }


    public static String hmacSHA512(String secretKey,final String data){
        try {
            if(secretKey == null  || data == null ){
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKeySpec = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKeySpec);

            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes); // sẽ tạo ra một mảng các byte : [-12, 45, 127, -89 ...]

            //chuyển đổi mảng byte khó hiểu thành một chuỗi ký tự chữ và số (Hệ thập lục phân - Hexadecimal)
            // gồm các ký tự từ 0-9 và a-f
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {

                // kiểu byte có giá trị từ -128 đến 127 (có dấu âm).
                // Nhưng để hiển thị đúng mã Hex, cần giá trị dương từ 0 đến 255.
                // Phép toán bitwise & 0xff giúp "cắt bỏ" phần dấu âm, ép nó về số dương chuẩn
                sb.append(String.format("%02x", b & 0xff));
                //  %02x: Định dạng con số đó thành đúng 2 ký tự Hex.
                //  (Ví dụ: số 10 biến thành "0a" vì 10 < 16 nên sẽ thêm số 0 ở cuối, số 255 biến thành "ff", ...)
                //  số -12 sẽ biến thành 256-12=244 ->hex = f4 ...
            }
            return sb.toString();

            //Hàm sẽ trả về một chuỗi có độ dài chính xác 128 ký tự
            //(vì SHA-512 tạo ra 512 bit = 64 byte, mỗi byte biến thành 2 ký tự Hex -> 64 x 2 = 128 ký tự).

            //Ví dụ chuỗi trả về sẽ trông như thế này:
            //"a1b2c3d4e5f6g7h8... (dài 128 ký tự)"
        } catch (Exception e) {
            return "";
        }
    }

    public static String getIAddress (HttpServletRequest request){
        String ipAddress;
        try {
            ipAddress= request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAddress = "127.0.0.1";
        }
        return ipAddress;
    }

    // Hàm sinh mã ngẫu nhiên
    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
    }
