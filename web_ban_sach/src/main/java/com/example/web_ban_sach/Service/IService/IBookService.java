package com.example.web_ban_sach.Service.IService;

import com.example.web_ban_sach.DTO.Request.BookRequest;
import org.springframework.http.ResponseEntity;

public interface IBookService {
    ResponseEntity<?> addBook(BookRequest bookRequest);
    ResponseEntity<?> updateBook(BookRequest bookRequest, Long id);
}
