package com.example.web_ban_sach.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId",  nullable = false, unique = true)
    private long id;

    @Column(name = "name" , nullable = false)
    private String name;

    @ManyToMany(mappedBy = "categories")
    List<Book> books;


}
