//package com.example.web_ban_sach.Enum;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.example.web_ban_sach.Enum.Permission.*;
//
//@Getter
//@AllArgsConstructor
//public enum Role {
//    GUEST(Collections.emptySet()),
//    ADMIN(
//            Set.of(
//            ADMIN_READ,
//            ADMIN_CREATE,
//            ADMIN_UPDATE,
//            ADMIN_DELETE,
//            USER_READ
//        )
//    ),
//    USER(
//            Set.of(
//            USER_READ
//            )
//    );
//
//    public static Role toEnum(String item){
//        for(Role role : Role.values()){
//            if(role.toString().equals(item)){
//                return role;
//            }
//        }
//        return null;
//    }
//
//    public final Set<Permission> permissions;
//
//    public List<SimpleGrantedAuthority> getAuthorities() {
//        List<SimpleGrantedAuthority> authorities = getPermissions()
//                .stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
//                .collect(Collectors.toList());
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
//        return authorities;
//    }
//}
