package com.example.web_ban_sach.Repository;
import com.example.web_ban_sach.DTO.Response.MonthlyRevenueResponse;
import com.example.web_ban_sach.Entity.Order;
import com.example.web_ban_sach.Entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "order")
public interface OrderRepository extends JpaRepository<Order,Long> , JpaSpecificationExecutor<Order> {
    List<Order> findByUserAccountOrderByCreationDateDesc(UserAccount userAccount);
    Optional<Order> findByPaymentTxnRef(String txnRef);



    @Query("Select SUM (o.totalPrice) from Order o where o.status = 'PAID'")
    Double calculateTotalPrice();

    Long countByStatus(String status);

    @Query("SELECT new com.example.web_ban_sach.DTO.Response.MonthlyRevenueResponse(" +
            "MONTH(o.creationDate), SUM(o.totalPrice)) " +
            "FROM Order o " +
            "WHERE o.status = 'PAID' AND YEAR(o.creationDate) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(o.creationDate) " +
            "ORDER BY MONTH(o.creationDate)")
    List<MonthlyRevenueResponse> calculateMonthlyRevenue();
}
