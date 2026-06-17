package com.example.web_ban_sach.Service.ServiceImp;

import com.example.web_ban_sach.Config.VNPayConfig;
import com.example.web_ban_sach.DTO.Request.CheckOutRequest;
import com.example.web_ban_sach.DTO.Response.MonthlyRevenueResponse;
import com.example.web_ban_sach.DTO.Response.OrderDetailResponse;
import com.example.web_ban_sach.DTO.Response.OrderResponse;
import com.example.web_ban_sach.Entity.*;
import com.example.web_ban_sach.Repository.*;
import com.example.web_ban_sach.Service.IService.IOrderService;
import com.example.web_ban_sach.exception.Message;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> getMyOrders(Principal principal) {
        if(principal==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Vui lòng đăng nhập !"));
        }
        UserAccount userAccount = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<Order> orders =  orderRepository.findByUserAccountOrderByCreationDateDesc(userAccount);

        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {
            OrderResponse dto = new OrderResponse();
            dto.setId(order.getId());
            dto.setCreationDate(order.getCreationDate());
            dto.setDeliverAddress(order.getDeliverAddress());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setShippingPrice(order.getShippingPrice());
            dto.setPaymentMethod(order.getPayment().getNamePayment());
            dto.setDeliverMethod(order.getDeliver().getNameDeliver());

            List<OrderDetailResponse> detailDTOs = new ArrayList<>();
            for (OrderDetails detail : order.getOrderDetails()) {
                OrderDetailResponse detailDTO = new OrderDetailResponse();
                detailDTO.setBookName(detail.getBook().getName());
                detailDTO.setQuantity(detail.getQuantity());
                detailDTO.setPrice(detail.getPrice());
                detailDTOs.add(detailDTO);
            }
            dto.setOrderDetails(detailDTOs);
            orderResponses.add(dto);
        }
        return ResponseEntity.ok().body(orderResponses);
    }

    @Override
    public ResponseEntity<?> deleteOrder(Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Vui lòng đăng nhập !"));
        }
        UserAccount userAccount = userRepository.findByUsername(principal.getName()).orElseThrow();
        Order order = orderRepository.findById(id).orElseThrow();

        if (!order.getUserAccount().getUserId().equals(userAccount.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message("Bạn không có quyền xóa dơn hàng này !"));
        }

        for (OrderDetails orderDetail : order.getOrderDetails()) {
            Book book = orderDetail.getBook();
            book.setQuantity(book.getQuantity() + orderDetail.getQuantity());
        }
        orderRepository.delete(order); //tu dong xoa orderDetail theo 
        return ResponseEntity.ok(new Message("Đã hủy đơn hàng thành công!"));
    }

    @Override
    public ResponseEntity<?> getDashboard(Principal principal) {
        Double totalRevenue = orderRepository.calculateTotalPrice();
        if(totalRevenue == null){
            totalRevenue = 0.0;
        }
        Long successOrders = orderRepository.countByStatus("PAID");
        List<MonthlyRevenueResponse> monthlyRevenueResponses = orderRepository.calculateMonthlyRevenue();

        Map<String,Object> map = new HashMap<>();
        map.put("totalRevenue",totalRevenue);
        map.put("successOrders",successOrders);
        map.put("monthlyRevenueResponses",monthlyRevenueResponses);

        return ResponseEntity.ok().body(map);
    }
}
