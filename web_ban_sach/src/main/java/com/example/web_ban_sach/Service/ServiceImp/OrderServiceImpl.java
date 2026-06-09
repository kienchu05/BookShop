package com.example.web_ban_sach.Service.ServiceImp;

import com.example.web_ban_sach.DTO.Request.CheckOutRequest;
import com.example.web_ban_sach.DTO.Response.OrderDetailResponse;
import com.example.web_ban_sach.DTO.Response.OrderResponse;
import com.example.web_ban_sach.Entity.*;
import com.example.web_ban_sach.Repository.*;
import com.example.web_ban_sach.Service.IService.IOrderService;
import com.example.web_ban_sach.exception.Message;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private CartItemRepository cartItemRepository;
    private PaymentRepository paymentRepository;
    private DeliverRepository deliverRepository;
    private BookRepository bookRepository;

    @Override
    public ResponseEntity<?> checkout(CheckOutRequest request, Principal principal) {
        if(principal==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Vui lòng đăng nhập !"));
        }
        UserAccount userAccount = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<CartItem> cartItems =  cartItemRepository.findByUserAccount(userAccount);
        if(cartItems==null){
            return ResponseEntity.badRequest().body(new Message("Giỏ hàng trống !"));
        }

        //Tinh tong tien
        double total = 0;
        for (CartItem cartItem : cartItems) {
            total += cartItem.getBook().getPriceFinal() * cartItem.getQuantity();
        }
        //Lay ra phuong thuc thanh toan va giao hang
        Payment payment = paymentRepository.findById(request.getPaymentId()).orElseThrow(
                () -> new RuntimeException("Phương thức thanh toán không hợp lệ !"));
        Deliver deliver = deliverRepository.findById(request.getDeliverId()).orElseThrow(
                () -> new RuntimeException("Phương thức giao hàng không hợp lệ !"));

        //Tao don hang
        Order order = new Order();
        order.setUserAccount(userAccount);
        order.setPayment(payment);
        order.setDeliver(deliver);
        order.setPurchaseAddress(request.getPurchaseAddress());
        order.setDeliverAddress(request.getDeliverAddress());
        order.setCreationDate(LocalDateTime.now());
        order.setShippingPrice(deliver.getDeliverPrice());
        order.setTotalPrice(total +  deliver.getDeliverPrice());

        //Khoi tao List<> OrderDetails
        List<OrderDetails>  orderDetails = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setOrder(order);
            orderDetail.setBook(cartItem.getBook());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getBook().getPriceFinal());

            Book book = cartItem.getBook();
            long currentStock = book.getQuantity();
            int requestedQty = cartItem.getQuantity();

            if (currentStock < requestedQty) {
                // Tùy chọn: Quăng lỗi chặn đơn hàng nếu không đủ sách
                throw new RuntimeException("Sách '" + book.getName() + "' không đủ số lượng trong kho!");
            }
            book.setQuantity(currentStock - requestedQty);
            bookRepository.save(book);

            orderDetails.add(orderDetail);
        }

        order.setOrderDetails(orderDetails);

        // Lưu order vào db sẽ tự lưu luôn list OrderDetail do cascadeType.ALL
        orderRepository.save(order);

        // Dọn sạch giỏ hàng
        cartItemRepository.deleteAll(cartItems);

        return ResponseEntity.ok().body(new Message("Đặt hàng thành công !"));
    }

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
}
