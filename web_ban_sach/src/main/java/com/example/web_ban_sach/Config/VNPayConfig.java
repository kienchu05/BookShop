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
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
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
