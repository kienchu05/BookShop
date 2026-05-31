package com.example.web_ban_sach.Repository;

import com.example.web_ban_sach.Entity.Deliver;
import com.example.web_ban_sach.Entity.LovedList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "lovedList")
public interface LovedListRepository extends JpaRepository<LovedList,Long> , JpaSpecificationExecutor<LovedList> {
}
