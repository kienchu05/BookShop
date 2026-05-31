package com.example.web_ban_sach.Repository;

import com.example.web_ban_sach.Entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "order-detail")
public interface OrderDetailRepository extends JpaRepository<OrderDetails,Long> , JpaSpecificationExecutor<OrderDetails> {
}
