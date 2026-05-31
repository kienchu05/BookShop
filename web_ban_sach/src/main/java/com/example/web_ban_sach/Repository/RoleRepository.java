package com.example.web_ban_sach.Repository;

import com.example.web_ban_sach.Entity.Roles;
//import com.example.web_ban_sach.Enum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "roles")
public interface RoleRepository extends JpaRepository<Roles, Long> , JpaSpecificationExecutor<Roles> {
}
