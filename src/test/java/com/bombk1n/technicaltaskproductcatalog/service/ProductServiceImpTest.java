package com.bombk1n.technicaltaskproductcatalog.service;

import com.bombk1n.technicaltaskproductcatalog.dto.ProductRequest;
import com.bombk1n.technicaltaskproductcatalog.dto.ProductResponse;
import com.bombk1n.technicaltaskproductcatalog.exception.ProductNotFoundException;
import com.bombk1n.technicaltaskproductcatalog.model.Product;
import com.bombk1n.technicaltaskproductcatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImp productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setCategory("Electronics");
        product.setStock(10);
        product.setCreatedDate(LocalDateTime.now());
        product.setLastUpdatedDate(LocalDateTime.now());

        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(BigDecimal.valueOf(99.99));
        productRequest.setCategory("Electronics");
        productRequest.setStock(10);

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Test Product");
        productResponse.setDescription("Test Description");
        productResponse.setPrice(BigDecimal.valueOf(99.99));
        productResponse.setCategory("Electronics");
        productResponse.setStock(10);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should return page of products when getAll is called")
    void getAll_ShouldReturnPageOfProducts() {
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        Page<ProductResponse> result = productService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(productResponse, result.getContent().get(0));
        verify(productRepository).findAll(pageable);
        verify(modelMapper).map(any(Product.class), eq(ProductResponse.class));
    }

    @Test
    @DisplayName("Should return product when getById is called with valid id")
    void getById_WithValidId_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        ProductResponse result = productService.getById(1L);

        assertNotNull(result);
        assertEquals(productResponse, result);
        verify(productRepository).findById(1L);
        verify(modelMapper).map(any(Product.class), eq(ProductResponse.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when getById is called with invalid id")
    void getById_WithInvalidId_ShouldThrowProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getById(99L)
        );
        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository).findById(99L);
    }

    @Test
    @DisplayName("Should create and return product when create is called")
    void create_ShouldCreateAndReturnProduct() {
        Product newProduct = new Product();
        when(modelMapper.map(eq(productRequest), eq(Product.class))).thenReturn(newProduct);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        ProductResponse result = productService.create(productRequest);

        assertNotNull(result);
        assertEquals(productResponse, result);
        verify(modelMapper).map(eq(productRequest), eq(Product.class));
        verify(productRepository).save(any(Product.class));
        verify(modelMapper).map(any(Product.class), eq(ProductResponse.class));

        verify(productRepository).save(productCaptor.capture());
        assertNotNull(productCaptor.getValue().getCreatedDate());
        assertNotNull(productCaptor.getValue().getLastUpdatedDate());
    }

    @Test
    @DisplayName("Should update and return product when update is called with valid id")
    void update_WithValidId_ShouldUpdateAndReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        ProductResponse result = productService.update(1L, productRequest);

        assertNotNull(result);
        assertEquals(productResponse, result);

        verify(productRepository).findById(1L);
        verify(productRepository).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertEquals(productRequest.getName(), capturedProduct.getName());
        assertEquals(productRequest.getDescription(), capturedProduct.getDescription());
        assertEquals(productRequest.getPrice(), capturedProduct.getPrice());
        assertEquals(productRequest.getCategory(), capturedProduct.getCategory());
        assertEquals(productRequest.getStock(), capturedProduct.getStock());
        assertNotNull(capturedProduct.getLastUpdatedDate());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when update is called with invalid id")
    void update_WithInvalidId_ShouldThrowProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.update(99L, productRequest)
        );
        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository).findById(99L);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete product when delete is called with valid id")
    void delete_WithValidId_ShouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when delete is called with invalid id")
    void delete_WithInvalidId_ShouldThrowProductNotFoundException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.delete(99L)
        );
        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository).existsById(99L);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should return products by category when getByCategory is called")
    void getByCategory_ShouldReturnProductsByCategory() {
        String category = "Electronics";
        List<Product> products = List.of(product);

        when(productRepository.findByCategory(category)).thenReturn(products);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        List<ProductResponse> result = productService.getByCategory(category);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productResponse, result.get(0));
        verify(productRepository).findByCategory(category);
        verify(modelMapper).map(any(Product.class), eq(ProductResponse.class));
    }

    @Test
    @DisplayName("Should return empty list when getByCategory is called with category that has no products")
    void getByCategory_WithNoProducts_ShouldReturnEmptyList() {
        String category = "NonExistentCategory";
        when(productRepository.findByCategory(category)).thenReturn(List.of());

        List<ProductResponse> result = productService.getByCategory(category);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findByCategory(category);
        verify(modelMapper, never()).map(any(), any());
    }
}