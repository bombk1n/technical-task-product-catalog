package com.bombk1n.technicaltaskproductcatalog.repository;

import com.bombk1n.technicaltaskproductcatalog.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category, Pageable pageable);

    List<Product> findByCategory(String category);
}
