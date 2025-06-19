package com.bombk1n.technicaltaskproductcatalog.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @Size(max = 2500, message = "Description must be at most 2500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0001", message = "Price must be greater than zero")
    private BigDecimal price;

    @Size(max = 255, message = "Category must be at most 255 characters")
    private String category;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
