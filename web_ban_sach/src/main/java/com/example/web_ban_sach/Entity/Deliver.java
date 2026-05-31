package com.example.web_ban_sach.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "deliver")
public class Deliver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliverId", nullable = false)
    private long deliverId;

    @Column(name = "name",length = 200,  nullable = false)
    private String nameDeliver;

    @Column(name = "description",nullable = false)
    private String descriptionDeliver;

    @Column(name = "deliverPrice", nullable = false )
    private double deliverPrice;

    @OneToMany(mappedBy = "deliver",  fetch = FetchType.LAZY)
    private List<Order> orders;
}
