package com.example.web_ban_sach.Repository;

import com.example.web_ban_sach.Entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "user-account")
public interface UserRepository extends JpaRepository<UserAccount, Long> , JpaSpecificationExecutor<UserAccount> {
}
