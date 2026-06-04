package com.example.web_ban_sach.Controller;

import com.example.web_ban_sach.DTO.Request.BookRequest;
import com.example.web_ban_sach.Service.IService.IBookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class BookController {
    private IBookService bookService;

    @PostMapping("/book/add-book")
    public ResponseEntity<?> addBook(@RequestBody BookRequest bookRequest){
        return bookService.addBook(bookRequest);
    }

    @PutMapping("/book/update-book/{id}")
    public ResponseEntity<?> updateBook(@RequestBody BookRequest bookRequest, @PathVariable("id") Long id){
        return bookService.updateBook(bookRequest, id);
    }
}
