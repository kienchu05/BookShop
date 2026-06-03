package com.example.web_ban_sach.Repository;

import com.example.web_ban_sach.Entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "book")
public interface BookRepository extends JpaRepository<Book, Long> , JpaSpecificationExecutor<Book> {
    Page<Book> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    //ContainingIgnoreCase có thể tìm kiếm 1 cách chính xác

    Page<Book> findByAuthorContainingIgnoreCase(@Param("author") String author, Pageable pageable);

    Page<Book> findByCategories_Id(@Param("id") long id , Pageable pageable);
}
