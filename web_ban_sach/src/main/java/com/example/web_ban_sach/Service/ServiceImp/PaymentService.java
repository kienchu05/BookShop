package com.example.web_ban_sach.Service.ServiceImp;

import com.example.web_ban_sach.Config.VNPayConfig;
import com.example.web_ban_sach.DTO.Request.CheckOutRequest;
import com.example.web_ban_sach.Entity.*;
import com.example.web_ban_sach.Repository.*;
import com.example.web_ban_sach.Service.IService.IPaymentService;
import com.example.web_ban_sach.exception.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService implements IPaymentService {

    @Value("${vnp.tmnCode}")
    private String vnp_TmnCode;
    @Value("${vnp.hashSecret}")
    private String vnp_HashSecret;
    @Value("${vnp.payUrl}")
    private String vnp_Url;
    @Value("${vnp.returnUrl}")
    private String vnp_ReturnUrl;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository;
    private final DeliverRepository deliverRepository;
    private final BookRepository bookRepository;

    @Override
    public ResponseEntity<?> checkout(CheckOutRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Vui lòng đăng nhập !"));
        }
        UserAccount userAccount = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findByUserAccount(userAccount);
        if (cartItems == null) {
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
        order.setTotalPrice(total + deliver.getDeliverPrice());

        if(payment.getPaymentId() == 5){
            order.setStatus("PENDING");
        }else{
            order.setStatus("PAID");
        }

        //Khoi tao List<> OrderDetails
        List<OrderDetails> orderDetails = new ArrayList<>();

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
                // Tùy chọn: Throw lỗi chặn đơn hàng nếu không đủ sách
                throw new RuntimeException("Sách '" + book.getName() + "' không đủ số lượng trong kho!");
            }
            book.setQuantity(currentStock - requestedQty);
            bookRepository.save(book);

            orderDetails.add(orderDetail);
        }

        order.setOrderDetails(orderDetails);
        String txnRef = "";
        if (payment.getPaymentId() == 5) {
            txnRef = VNPayConfig.getRandomNumber(8);
            order.setPaymentTxnRef(txnRef); //phải lưu lại mã này (vì nó duy nhất nên có thể truy vấn dc)
        }

        // Lưu order vào db sẽ tự lưu luôn list OrderDetail do cascadeType.ALL
        orderRepository.save(order);
        // Dọn sạch giỏ hàng
        cartItemRepository.deleteAll(cartItems); //xóa danh sách cartItems

        if (payment.getPaymentId() == 5) {
            long amount = (long) (order.getTotalPrice() * 100); // VNPay yêu cầu số tiền nhân 100
            String vnp_TxnRef = txnRef;
            String vnp_TmnCode = this.vnp_TmnCode;
            String vnp_HashSecret = this.vnp_HashSecret;
            String vnp_Url = this.vnp_Url;
            String vnp_ReturnUrl = this.vnp_ReturnUrl;


            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang hoa don so: " + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", "127.0.0.1");

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            // Sắp xếp các tham số theo thứ tự alphabet của key
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            try {
                for (String fieldName : fieldNames) {
                    String fieldValue = vnp_Params.get(fieldName);

                    if (fieldValue != null && fieldValue.length() > 0) {
                        // Nếu chuỗi đã có dữ liệu trước đó, tự động thêm dấu '&' làm phân tách
                        if (query.length() > 0) {
                            query.append("&");
                            hashData.append("&");
                        }
                        // Build Hash Data
                        hashData.append(fieldName);
                        hashData.append('=');
                        hashData.append(java.net.URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                        // Build Query
                        query.append(java.net.URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                        query.append('=');
                        query.append(java.net.URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    }
                }
            } catch (Exception e) {
                // In ra lỗi nếu máy chủ không hỗ trợ bảng mã
                log.error("Lỗi mã hóa tham số VNPay: ", e);
            }

            String queryUrl = query.toString();

            //Dùng thuật toán bảo mật HMAC-SHA512 cùng chìa khóa bí mật (vnp_HashSecret)
            // băm chuỗi hashData thành một chữ ký điện tử
            String vnp_SecureHash = VNPayConfig.hmacSHA512(vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = vnp_Url + "?" + queryUrl;

            // Trả về đối tượng chứa URL để React chuyển hướng
            Map<String, String> result = new HashMap<>();
            result.put("status", "PAYMENT_REDIRECT");
            result.put("url", paymentUrl);
            return ResponseEntity.ok(result);
        }

        // Nếu là COD, trả về cấu trúc thông báo thành công thông thường

        Map<String, String> result = new HashMap<>();
        result.put("status", "SUCCESS");
        result.put("message", "Đặt hàng thành công !");
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> confirmPayment(HttpServletRequest request) {
        log.info("Chuỗi Query String nguyên gốc: {}", request.getQueryString());
        // 1. Tự tay lấy các tham số nguyên gốc từ Request để tránh Spring Boot làm sai lệch
        Map<String, String> fields = new HashMap<>();

        //numeration<String> params = request.getParameterNames() :
        //chỉ liệt kê tên của tất cả các trường (key) mà VNPay gửi về: [vnp_Amount, vnp_TxnRef, vnp_BankCode, vnp_ResponseCode, ...].
        //Nó chỉ chứa tên, chưa hề chứa giá trị.

        //params.hasMoreElements() : tìm lần lượt từ trên xuống dưới đối với mỗi một key trong mảng response của VNPay
        //Nếu còn thì đi tiếp vào trong vòng lặp.
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement(); //đọc cái tên tiếp theo trên danh sách. Ví dụ: fieldName = "vnp_Amount".
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                fields.put(fieldName, fieldValue);
            }
        }

        // 2. Lấy chữ ký do VNPay gửi về
        String vnp_SecureHash = fields.get("vnp_SecureHash");

        // 3. Xóa chữ ký ra khỏi Map trước khi băm lại (Theo đúng tài liệu VNPay)
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // 4. Tính toán lại chữ ký
        String signValue = VNPayConfig.hashAllFields(fields, this.vnp_HashSecret);

        // Debug
        log.info("Hash VNPay gửi về: {}", vnp_SecureHash);
        log.info("Hash Backend tự tính: {}", signValue);

        // 5. Đối chiếu chữ ký
        if (signValue.equals(vnp_SecureHash)) {
            String responseCode = fields.get("vnp_ResponseCode");
            String orderId = fields.get("vnp_TxnRef"); // Mã hóa đơn của bạn

            if ("00".equals(responseCode)) {
                Order order = orderRepository.findByPaymentTxnRef(orderId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng !"));
                order.setStatus("PAID");
                orderRepository.save(order);
                return ResponseEntity.ok(Collections.singletonMap("message", "Thanh toán thành công"));
            } else {
                Order order = orderRepository.findByPaymentTxnRef(orderId).orElse(null);
                if (order != null && !"CANCELLED".equals(order.getStatus())) {
                    order.setStatus("CANCELLED"); // Hoặc "FAILED"
                    orderRepository.save(order);

                    //neu bi loi trong qua trinh thanh toan thi phai hoan lai so luong sach
                    for(OrderDetails orderDetails : order.getOrderDetails()) {
                        Book book = orderDetails.getBook();
                        book.setQuantity(orderDetails.getQuantity() + book.getQuantity());
                    }
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Giao dịch bị hủy hoặc thất bại!"));
                }
            }
        }
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Chữ ký số không hợp lệ, dữ liệu bị can thiệp!"));
    }
}
