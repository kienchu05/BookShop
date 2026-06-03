/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.web_ban_sach.exception;


import java.time.LocalDateTime;
import java.util.Map;

/**
 *
 * @author User
 */
public class ErrorResponse {
    private String timeStamp;
    private String message;
    private Map<String, String> detail;

    public ErrorResponse(String timeStamp, String message, Map<String, String> detail) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.detail = detail;
    }
    
    public ErrorResponse(String message, Map<String, String> detail) {
        this.timeStamp = LocalDateTime.now().toString();
        this.message = message;
        this.detail = detail;
    }
    
    
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, String> detail) {
        this.detail = detail;
    }
    
    
}
