package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.DTO.Request.BookRequest;
import com.example.web_ban_sach.Service.IService.IBookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BookController {
    private IBookService bookService;

    @PostMapping("/book/add-book")
    public ResponseEntity<?> addBook(@RequestBody BookRequest bookRequest){
        return bookService.addBook(bookRequest);
    }
}
