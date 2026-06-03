package com.example.web_ban_sach.Repository;

import com.example.web_ban_sach.Entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "user-account")
public interface UserRepository extends JpaRepository<UserAccount, Long> , JpaSpecificationExecutor<UserAccount> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findByActivatedCode(String activationCode);
}
