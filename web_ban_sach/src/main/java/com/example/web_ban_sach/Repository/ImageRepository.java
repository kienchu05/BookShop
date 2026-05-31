package com.example.web_ban_sach.Repository;

import com.example.web_ban_sach.Entity.Deliver;
import com.example.web_ban_sach.Entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "image")
public interface ImageRepository extends JpaRepository<Image,Long> , JpaSpecificationExecutor<Image> {
}
