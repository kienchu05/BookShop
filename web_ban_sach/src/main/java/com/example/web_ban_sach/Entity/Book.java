package com.example.web_ban_sach.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookId", nullable = false)
    private long id;

    @Column(name = "name" , length = 100,  nullable = false)
    private String name;

    @Column(name = "author", length = 100,  nullable = false)
    private String author;

    @Column(name = "isbn", length = 100,  nullable = false)
    private String isbn;

    @Column(name = "priceInit", nullable = false)
    private double priceInit;

    @Column(name = "priceFinal", nullable = false)
    private double priceFinal;

    @Column(name = "description" , columnDefinition = "text")
    private String description;

    @Column(name = "quantity")
    private long quantity;

    @Column(name = "avgRating")
    private double avgRating;

    @ManyToOne(fetch = FetchType.LAZY,  cascade = {
            CascadeType.PERSIST,  CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH
    })
            @JoinTable(
                    name = "book_category",
                    joinColumns = @JoinColumn(name = "bookId"),
                    inverseJoinColumns = @JoinColumn(name = "categoryId")
            )
    List<Category> categories;

    @OneToMany(mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true )//Hibernate tự dọn dẹp ảnh tồn dư)
    List<Image> images;

    @OneToMany(mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true )
    List<RatingBook> ratingBooks;

    @OneToMany(mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true )
    List<OrderDetails> orderDetails;

    @OneToMany(mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true )
    List<LovedList> lovedLists;
}
