package com.bombk1n.technicaltaskproductcatalog.service;

import com.bombk1n.technicaltaskproductcatalog.dto.ProductRequest;
import com.bombk1n.technicaltaskproductcatalog.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface ProductService {

    Page<ProductResponse> getAll(Pageable pageable);

    ProductResponse getById(Long id);

    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(Long id, ProductRequest productRequest);

    void delete(Long id);

    List<ProductResponse> getByCategory(String category);

}
