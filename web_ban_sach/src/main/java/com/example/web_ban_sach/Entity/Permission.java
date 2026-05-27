package com.example.web_ban_sach.Entity;

import jakarta.persistence.*;
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
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="permissionId", nullable=false, unique=true)
    private long id;

    @Column(name = "name" , length = 255,  nullable = false)
    private String name;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    List<UserAccount> userAccounts;
}
