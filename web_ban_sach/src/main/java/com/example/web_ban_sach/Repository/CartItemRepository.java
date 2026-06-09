package com.example.web_ban_sach.Repository;

import com.example.web_ban_sach.Entity.Book;
import com.example.web_ban_sach.Entity.CartItem;
import com.example.web_ban_sach.Entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> , JpaSpecificationExecutor<CartItem> {
    Optional<CartItem> findByUserAccountAndBook(UserAccount userAccount, Book book);
    List<CartItem> findByUserAccount(UserAccount userAccount);
}
