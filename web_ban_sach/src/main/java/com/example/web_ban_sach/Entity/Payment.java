package com.example.web_ban_sach.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name = "name",length = 200,  nullable = false)
    private String namePayment;

    @Column(name = "description",nullable = false)
    private String descriptionPayment;

    @JsonIgnore //Khi Spring Boot dùng thư viện Jackson để biến Java thành chữ JSON gửi cho React,
    // nó gặp Deliver thứ 1 nó chui vào lấy danh sách Order,
    // trong Order lại có Deliver nó lại lấy Order... Tạo thành một vòng lặp vô tận.
    @OneToMany(mappedBy = "payment",  fetch = FetchType.LAZY)
    private List<Order> orders;
}
