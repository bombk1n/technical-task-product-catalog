package com.bombk1n.technicaltaskproductcatalog.service;

import com.bombk1n.technicaltaskproductcatalog.dto.ProductRequest;
import com.bombk1n.technicaltaskproductcatalog.dto.ProductResponse;
import com.bombk1n.technicaltaskproductcatalog.exception.ProductNotFoundException;
import com.bombk1n.technicaltaskproductcatalog.model.Product;
import com.bombk1n.technicaltaskproductcatalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productCache", key = "#id")
    public ProductResponse getById(Long id) {
        return productRepository.findById(id)
                .map(this::mapEntityToResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest productRequest) {
        Product product = mapRequestToEntity(productRequest);
        product.setCreatedDate(LocalDateTime.now());
        product.setLastUpdatedDate(LocalDateTime.now());
        productRepository.save(product);
        return mapEntityToResponse(product);
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "productCache", key = "#id")
            },
            evict = {
                    @CacheEvict(value = "categoryProductsCache", key = "#result.category")
            }
    )
    public ProductResponse update(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setStock(productRequest.getStock());
        product.setLastUpdatedDate(LocalDateTime.now());
        product = productRepository.save(product);
        return mapEntityToResponse(product);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "productCache", key = "#id"),
                    @CacheEvict(value = "categoryProductsCache", allEntries = true)
            }
    )
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categoryProductsCache", key = "#category")
    public List<ProductResponse> getByCategory(String category) {
        return productRepository.findByCategory(category).stream().map(this::mapEntityToResponse).toList();
    }

    private ProductResponse mapEntityToResponse(Product productEntity) {
        return modelMapper.map(productEntity, ProductResponse.class);

    }

    private Product mapRequestToEntity(ProductRequest productRequest) {
        return modelMapper.map(productRequest, Product.class);
    }
}
