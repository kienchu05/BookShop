package com.example.web_ban_sach.Entity;

import com.example.web_ban_sach.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "userAccounts")
public class UserAccount implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", unique = true, nullable = false)
    private long userId;

    @Column(name = "name" , length = 100, nullable = false)
    private String name;

    @Column(name = "username" ,  length = 100, nullable = false, unique = true)
    private String userName;

    @Column(name = "password", length = 256, nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "address" ,  length = 200, nullable = false)
    private String address;

    @Column(name = "gender", length = 100)
    private String gender;

    @Column(name = "role", length = 50)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(name = "purchaseAddress", nullable = false)
    private String purchaseAddress;

    @Column(name = "deliverAddress", nullable = false)
    private String deliverAddress;

    @Column(name = "accessToken", length = 150, unique = true,  nullable = true)
    private String accessToken;

    @Column(name = "refreshToken" ,  length = 150, unique = true,  nullable = true)
    private String refreshToken;

    @OneToMany(mappedBy = "userAccount",
            cascade = CascadeType.ALL)
    List<Order> orders;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
            CascadeType.PERSIST,  CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH
    })
    @JoinTable(
            name = "user_permission",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "permissionId")
    )
    List<Permission> permissions;

    @OneToMany(mappedBy = "userAccount",
            cascade = CascadeType.ALL, orphanRemoval = true)
    List<RatingBook> ratingBooks;

    @OneToMany(mappedBy = "userAccount",
            cascade = CascadeType.ALL,orphanRemoval = true)
    List<LovedList> lovedLists;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();

    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
