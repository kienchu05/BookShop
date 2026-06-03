package com.example.web_ban_sach.Entity;

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

    @OneToMany(mappedBy = "payment",  fetch = FetchType.LAZY)
    private List<Order> orders;
}
