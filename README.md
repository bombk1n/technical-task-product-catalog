# Product Catalog Cache System

A RESTful web service for managing a product catalog with advanced caching to improve read performance. This project is designed as a technical task and implements CRUD operations for products, H2 in-memory database, Spring Cache, and other requirements per the specification.

---

# Setup Instructions

## Prerequisites

Make sure you have the following installed before you begin:

- [Docker](https://www.docker.com/)

## Running the Application with Docker Compose

To run project and its dependencies using Dockerfile:

### 1. Clone the Repository

```bash
git clone https://github.com/bombk1n/technical-task-product-catalog.git
```

### 2. Build the Docker Image

```bash
docker build -t product-project .
```

### 3. Start the Application

```bash
docker run -p 8080:8080 product-project
```

Application will be available on: `http://localhost:8080/`

API Documentation will be available on: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## API Endpoints

### Authentication (`/api/v1/auth`)

| Method | URL                    | Description         | Sample Valid Request Body         |
|--------|------------------------|---------------------|-----------------------------------|
| POST   | /api/v1/auth/register  | Register new user   | [JSON](#register)                 |
| POST   | /api/v1/auth/login     | Login user          | [JSON](#login)                    |

### Products (`/api/v1/products`)

| Method | URL                                   | Description                  | Sample Valid Request Body         |
|--------|---------------------------------------|------------------------------|-----------------------------------|
| GET    | /api/v1/products                      | Get all products (paginated) | -                                 |
| GET    | /api/v1/products/{id}                 | Get product by ID            | -                                 |
| POST   | /api/v1/products                      | Create a new product         | [JSON](#productcreate)            |
| PUT    | /api/v1/products/{id}                 | Update an existing product   | [JSON](#productupdate)            |
| DELETE | /api/v1/products/{id}                 | Delete a product             | -                                 |
| GET    | /api/v1/products/category/{category}  | Get products by category     | -                                 |

---

#### <a id="register">Register -> /api/v1/auth/register</a>
```json
{
    "username": "johnDoe123",
    "password": "securePassword123"
}

```

#### <a id="login">Login -> /api/v1/auth/login</a>
```json
{
"username": "johnDoe123",
"password": "securePassword123"
}

```

#### <a id="productcreate">Create Product -> /api/v1/products</a>
```json
{
"name": "Wireless Mouse",
"description": "Ergonomic wireless mouse with USB receiver.",
"price": 29.99,
"category": "Electronics",
"stock": 100
}
```
#### <a id="productupdate">Update Product -> /api/v1/products/{id}</a>
```json
{
"name": "Wireless Mouse Pro",
"description": "Upgraded ergonomic wireless mouse with Bluetooth.",
"price": 39.99,
"category": "Electronics",
"stock": 80
}
```
#### Product Response Example
```json
{
"id": 1,
"name": "Wireless Mouse",
"description": "Ergonomic wireless mouse with USB receiver.",
"price": 29.99,
"category": "Electronics",
"stock": 100,
"createdDate": "2025-06-20T10:00:00",
"lastUpdatedDate": "2025-06-20T10:00:00"
}
```
---

## Assumptions

- The project implements authentication using JWT (JSON Web Token).
- API rate limiting is applied, allowing a maximum of 100 requests per minute.
- Default pagination parameters are `page=0` and `size=10`.

---

## Technical Choices Explained

- **Spring Boot 3.x**: Rapid setup, modern Java, and production-ready features.
- **H2 Database**: Fast, in-memory, great for development and testing.
- **Spring Cache**: Simple caching abstraction; caches product and category queries.
- **Docker**: Easiest way to package and run the app universally.
- **Swagger/OpenAPI**: For interactive, self-documented REST API.
- **Validation**: Bean Validation (JSR-380) for input safety.
- **Exception Handling**: Global exception handler for clean API errors.

---