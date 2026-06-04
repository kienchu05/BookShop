package com.example.web_ban_sach.DTO.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Builder
@Getter
@Setter
public class BookRequest {
    @NotEmpty(message = "Tên sách không được để trống !")
    private String name;
    @NotEmpty(message = "Tên tác giả không được để trống !")
    private String author;
    @NotEmpty(message = "Mã xuất bản không được để trống !")
    private String isbn;
    @NotEmpty(message = "Số lượng không được để trống !")
    private long quantity;
    @NotEmpty(message = "Mô tả không được để trống !")
    private String description;
    @NotEmpty(message = "Giá niêm yết không được để trống !")
    private double priceInit;
    @NotEmpty(message = "Giá bán không được để trống !")
    private double priceFinal;

    private double avgRating;

    @NotEmpty(message = "Thể loại không được để trống !")
    private List<String> categories;

    private List<String> images;
}
