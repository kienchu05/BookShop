package com.example.web_ban_sach.Repository;

import com.example.web_ban_sach.Entity.Order;
import com.example.web_ban_sach.Entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(path = "order")
public interface OrderRepository extends JpaRepository<Order,Long> , JpaSpecificationExecutor<Order> {
    List<Order> findByUserAccountOrderByCreationDateDesc(UserAccount userAccount);
}
