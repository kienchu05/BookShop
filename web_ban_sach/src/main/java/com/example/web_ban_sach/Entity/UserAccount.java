package com.example.web_ban_sach.Entity;

//import com.example.web_ban_sach.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "userAccounts")
public class UserAccount implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "name" , length = 100, nullable = false)
    private String name;

    @Column(name = "username" ,  length = 100, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 256, nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "address" , length = 200, nullable = true)
    private String address;

    @Column(name = "gender", length = 100, nullable = true)
    private String gender;

    @Column(name = "purchaseAddress")
    private String purchaseAddress;

    @Column(name = "deliverAddress")
    private String deliverAddress;

    @Column(name = "accessToken", unique = true,  nullable = true)
    private String accessToken;

    @Column(name = "refreshToken",  unique = true,  nullable = true)
    private String refreshToken;

    @Column(name = "isActivated")
    private Boolean isActivated = false;

    @Column(name = "activatedCode")
    private String activatedCode;

    @OneToMany(mappedBy = "userAccount",
            cascade = CascadeType.ALL)
    List<Order> orders;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
            CascadeType.MERGE,
            CascadeType.REFRESH
    })
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
            @JsonIgnore
    List<Roles> roles;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "userAccount",
            cascade = CascadeType.ALL, orphanRemoval = true)
    List<RatingBook> ratingBooks;

    @OneToMany(mappedBy = "userAccount",
            cascade = CascadeType.ALL,orphanRemoval = true)
    List<LovedList> lovedLists;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = this.roles.stream().map(
                role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase())
        ).collect(Collectors.toList());
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActivated != null && this.isActivated;
    }
}
