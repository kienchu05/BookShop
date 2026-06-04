package com.example.web_ban_sach.Service.ServiceImp;

import com.example.web_ban_sach.DTO.Request.BookRequest;
import com.example.web_ban_sach.Entity.Book;
import com.example.web_ban_sach.Entity.Category;
import com.example.web_ban_sach.Repository.BookRepository;
import com.example.web_ban_sach.Repository.CategoryRepository;
import com.example.web_ban_sach.Service.IService.IBookService;
import com.example.web_ban_sach.exception.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@Data
public class BookServiceImp implements IBookService {
    private BookRepository bookRepository;
    private CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<?> addBook(BookRequest bookRequest) {
        try {
            if (bookRepository.existsByIsbn(bookRequest.getIsbn())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Message("Lỗi: Mã ISBN '" + bookRequest.getIsbn() + "' đã tồn tại trong hệ thống!"));
            }

            // Kiểm tra Tên sách
            if (bookRepository.existsByName(bookRequest.getName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Message("Lỗi: Cuốn sách mang tên '" + bookRequest.getName() + "' đã tồn tại!"));
            }
            Book newBook = new Book();
            newBook.setName(bookRequest.getName());
            newBook.setAuthor(bookRequest.getAuthor());
            newBook.setIsbn(bookRequest.getIsbn());
            newBook.setPriceInit(bookRequest.getPriceInit());
            newBook.setPriceFinal(bookRequest.getPriceFinal());
            newBook.setDescription(bookRequest.getDescription());
            newBook.setQuantity(bookRequest.getQuantity());
            newBook.setAvgRating(0.0);

            if (bookRequest.getCategories() != null) {
                List<Category> categoryList = new ArrayList<>();

                for (String name : bookRequest.getCategories()) {
                    // Tìm thể loại trong DB theo tên
                    categoryRepository.findByNameIgnoreCase(name).ifPresent(category -> {
                        categoryList.add(category); // Nếu tìm thấy thì thêm vào list
                    });
                }
                newBook.setCategories(new ArrayList<>(categoryList) {
                });
            }

            // Thực thi lệnh lưu
            bookRepository.save(newBook);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Message("Thêm sách mới thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message("Lỗi hệ thống khi lưu sách: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> updateBook(BookRequest bookRequest, Long id) {
        try {
            Optional<Book> optionalBook = bookRepository.findById(id);
            if (optionalBook.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Message("Lỗi: Không tìm thấy sách với ID = " + id));
            }
            Book book = optionalBook.get();
            book.setIsbn(bookRequest.getIsbn());
            book.setName(bookRequest.getName());
            book.setAuthor(bookRequest.getAuthor());
            book.setPriceInit(bookRequest.getPriceInit());
            book.setPriceFinal(bookRequest.getPriceFinal());
            book.setDescription(bookRequest.getDescription());
            book.setQuantity(bookRequest.getQuantity());
            book.setAvgRating(bookRequest.getAvgRating());
            if (bookRequest.getCategories() != null) {
                List<Category> categoryList = new ArrayList<>();
                for (String name : bookRequest.getCategories()) {
                    categoryRepository.findByNameIgnoreCase(name).ifPresent(category -> {
                        categoryList.add(category);
                    });
                }
                book.setCategories(new ArrayList<>(categoryList) {
                });
            }
            bookRepository.save(book);
            return ResponseEntity.ok(new Message("Cập nhật sách thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message("Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
