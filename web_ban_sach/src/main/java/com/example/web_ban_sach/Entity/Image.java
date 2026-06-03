package com.example.web_ban_sach.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100,  nullable = false)
    private String name;

    @Column(name = "isAvatar")
    private boolean isAvatar;

    @Column(name = "link")
    private String linkToImage;

    @Column(name = "data" , columnDefinition = "LONGTEXT")
    @Lob //Dùng để lưu trữ văn bản (text) có kích thước khổng lồ.
    private String dataImage;

    @ManyToOne( fetch = FetchType.LAZY ,cascade = {
            CascadeType.PERSIST,  CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH
    })
    @JoinColumn(name = "book_id",  nullable = false) // Auto map vào khóa chính (PK)
    private Book book;
}
