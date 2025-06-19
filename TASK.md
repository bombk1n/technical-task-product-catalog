# Technical Task: Product Catalog Cache System

## Overview
Create a RESTful web service for managing a product catalog with caching capabilities to improve read performance. The system should handle basic CRUD operations for products and implement appropriate caching strategies.

**Please read this task carefully. Only after you have read the entire task, collected all the questions, you have the right to contact Dmitry @d_osypchuk via telegram**

## Technical Requirements

### Core Technologies
- Java 17+
- Spring Boot 3.x
- H2 Database (in-memory)
- Spring Cache
- Maven/Gradle (your choice)
- JUnit 5 for testing

### Entity Structure
Product entity should contain:
- ID (Long)
- Name (String, not null)
- Description (String)
- Price (BigDecimal, not null)
- Category (String)
- Stock (Integer)
- Created Date (LocalDateTime)
- Last Updated Date (LocalDateTime)

## Required Functionality

### 1. REST API Endpoints:
- `GET /api/v1/products` - List all products (paginated)
- `GET /api/v1/products/{id}` - Get single product
- `POST /api/v1/products` - Create new product
- `PUT /api/v1/products/{id}` - Update product
- `DELETE /api/v1/products/{id}` - Delete product
- `GET /api/v1/products/category/{category}` - Get products by category

### 2. Caching Requirements:
- Implement caching for single product retrieval
- Implement caching for category-based product lists
- Cache eviction on product updates and deletes
- Cache timeout of 10 minutes

### 3. Database:
- Use H2 in-memory database
- Implement database initialization with sample data (Can be used Flyway)
- Create appropriate indexes for optimal query performance

### 4. Additional Features:
- Basic input validation
- Error handling with appropriate HTTP status codes
- Simple API documentation (Swagger/OpenAPI)

## Acceptance Criteria

### 1. Code Quality:
- Clean code structure following SOLID principles
- Proper exception handling
- Meaningful variable/method names

### 2. Documentation:
- README.md with setup instructions
- API documentation
- Basic architecture explanation

## Optional Bonus Tasks
- Implement API rate limiting
- Add request/response logging
- Implement basic authentication
- Add metrics for cache hit/miss ratio
- Testing

## Submission Guidelines
1. Upload code to a Git repository
2. Include a README with:
    - Setup instructions
    - API documentation
    - Any assumptions made
    - Explanation of technical choices
3. Provide access to the repository to reviewers

## Time Estimation
- Expected completion time: 1-2 working days