package com.bombk1n.technicaltaskproductcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TechnicalTaskProductCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechnicalTaskProductCatalogApplication.class, args);
    }

}
