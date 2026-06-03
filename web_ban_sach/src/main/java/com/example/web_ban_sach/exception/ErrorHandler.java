/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.web_ban_sach.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.databind.ObjectMapper;
import com.example.web_ban_sach.exception.ErrorResponse;

/**
 *
 * @author User
 */
//danh dau class xu li loi
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler implements
        AuthenticationEntryPoint,
        AccessDeniedHandler {

    @Override
    //xử lí khi người dùng đăng nhập thành công nhưng bị từ chối 1 số quyền
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        var message = "Quyền truy cập bị từ chối";
        var error = new ErrorResponse(message, null);
        var out = response.getOutputStream();

        new ObjectMapper().writeValue(out, error);
    }

    @Override
    //Đăng nhập với tài khoản hoặc mật khẩu không đúng
    public void commence(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        var message = "Tài khoản hoặc mật khẩu không đúng";
        var error = new ErrorResponse(message, null);
        var out = response.getOutputStream();

        new ObjectMapper().writeValue(out, error);
    }

    @Override
    public ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        //e.getRequiredType(): Đây là phương thức trả về một đối tượng Class<?>
        //(đại diện cho kiểu dữ liệu mà Spring mong đợi, ví dụ: Long.class, Integer.class).
        if (exception instanceof MethodArgumentTypeMismatchException e) {
            String name = e.getName(); //Tên tham số (ví dụ: id)
            String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
            String message = String.format("Tham số '%s' phải có kiểu dữ liệu là %s", name, requiredType);
            ErrorResponse errorResponse = new ErrorResponse(message, null);
            return new ResponseEntity<>(errorResponse, headers, status);
        }
        return super.handleTypeMismatch(exception, headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String message = "Missing parameters";
        ErrorResponse errorResponse = new ErrorResponse(message, null);
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String message = "Method type not supported";
        ErrorResponse errorResponse = new ErrorResponse(message, null);
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String message = "Method not supported";
        ErrorResponse errorResponse = new ErrorResponse(message, null);
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String message = "Dữ liệu không hợp lệ !";
        //nó giữ nguyên thứ tự bạn thêm lỗi vào. Khi gửi lỗi về cho người dùng,
        //các trường lỗi sẽ xuất hiện đúng thứ tự như khi họ điền vào form,
        //giúp người dùng dễ theo dõi hơn.
        Map<String, String> details = new LinkedHashMap<String, String>();
        for (FieldError error : ex.getFieldErrors()) {
            String key = error.getField();
            String value = error.getDefaultMessage();
            details.put(key, value);
        }
        ErrorResponse errorResponse = new ErrorResponse(message, details);
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        String message = "Dữ liệu không hợp lệ !";
        Map<String, String> details = new LinkedHashMap<>();
        for (ConstraintViolation<?> error : exception.getConstraintViolations()) {
            String key = error.getPropertyPath().toString();
            String value = error.getMessage();
            details.put(key, value);
        }
        ErrorResponse error = new ErrorResponse(message, details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
