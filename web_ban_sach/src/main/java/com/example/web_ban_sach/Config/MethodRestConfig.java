package com.example.web_ban_sach.Config;

import com.example.web_ban_sach.Entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MethodRestConfig implements RepositoryRestConfigurer {
    private EntityManager entityManager;

    // Inject EntityManager để tự động lấy tất cả các Entity trong dự án
    public MethodRestConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    private String url = "http://localhost:3000";

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        //Tự động lấy ra id của entity khi dữ liệu được trả về dưới dạng JSON
        config.exposeIdsFor(entityManager.getMetamodel().getEntities().stream().map(Type::getJavaType).toArray(Class[]::new));

        //CORS Configuration
        cors.addMapping("/**") // áp dụng cho mọi api trong dự án , /books, /categories ...
                .allowedOrigins(url)
                .allowedMethods("PUT", "GET", "POST", "DELETE");

        HttpMethod[] blockHttpMethods = {HttpMethod.PATCH, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE};
        blockHttpMethods(Category.class, config, blockHttpMethods);
    }

    private void blockHttpMethods(Class c, //Class nào sẽ bị giới hạn HttpMethod
                                  RepositoryRestConfiguration config,
                                  HttpMethod[] httpMethod) {
        config.getExposureConfiguration()
                .forDomainType(c) // cấu hình sẽ áp dụng cho Class c cụ thể , các class (Entity) khác không bị ảnh hưởng

                //Để chặn các method khi người dùng truy cập vào 1 bản ghi cụ thể (vd : http://localhost:8080/categories/1)
                // Các hành vi như DELETE (Xóa) 1 bản ghi nào đó , PUT (chỉnh sửa) 1 bản ghi nào đó (thoong qua id)
                .withItemExposure(((metdata, httpMethods) ->  httpMethods.disable(httpMethod)))

                //chặn các method khi người dùng truy cập vào 1 danh sách các bản ghi (vd : http://localhost:8080/categories) như hành vi POST(Tạo mới bản ghi)
                .withCollectionExposure(((metdata, httpMethods) ->  httpMethods.disable(httpMethod)));
    }
}
