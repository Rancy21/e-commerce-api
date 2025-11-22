# E-Commerce API

A robust E-commerce REST API built with Spring Boot 3.5, featuring secure authentication (JWT & OAuth2), product management, shopping cart functionality, and payment integration (Stripe & PayPal).

## 🚀 Features

- **User Management**:
  - User registration and authentication (Login/Register).
  - Role-based access control (Admin/User).
  - Profile updates (Name, Password).
  - OAuth2 Login support (Google, GitHub).
  - Password Reset via Email.
- **Product Catalog**:
  - Create, read, update, and delete products.
  - Categorize products.
  - Stock management (increase/decrease quantity).
  - Search products by name or category.
- **Shopping Cart**:
  - Add/Remove items to/from cart.
  - Update item quantities.
  - View cart details.
- **Order & Payments**:
  - Secure checkout process.
  - **Stripe** integration (Checkout sessions, Webhooks).
  - **PayPal** integration (Sandbox support).

## 🛠️ Tech Stack

- **Java**: 21
- **Framework**: Spring Boot 3.5.8
- **Database**: MySQL
- **Security**: Spring Security, JWT (JSON Web Tokens), OAuth2 Client
- **Build Tool**: Maven
- **Utilities**: Lombok, Gson, JavaMailSender

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java Development Kit (JDK) 21**
- **Maven 3.8+**
- **MySQL Server 8.0+**

## ⚙️ Configuration

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/Rancy21/e-commerce-api.git
    cd e-commerce-api
    ```

2.  **Database Setup:**
    Create a MySQL database named `e_commerce_db`.

    ```sql
    CREATE DATABASE e_commerce_db;
    ```

3.  **Application Configuration:**
    Open `src/main/resources/application.yaml` and update the following configurations:

    - **Database Credentials:**
      ```yaml
      spring:
        datasource:
          username: YOUR_DB_USERNAME # Default: root
          password: YOUR_DB_PASSWORD
      ```
    - **Mail Server (for password reset):**
      ```yaml
      spring:
        mail:
          username: your-email@gmail.com
          password: your-app-password
      ```
    - **OAuth2 (Optional - for Social Login):**
      Fill in your Client IDs and Secrets for Google and GitHub.
    - **JWT Secret:**
      Change the `jwt.secret` to a secure random string.
    - **Payment Gateways:**
      - **PayPal**: Add your Client ID and Secret.
      - **Stripe**: Add your API Key and Webhook Secret.

## 🏃‍♂️ How to Run

1.  **Build the project:**

    ```bash
    mvn clean install
    ```

2.  **Run the application:**

    ```bash
    mvn spring-boot:run
    ```

    The API will be available at `http://localhost:8080`.

## 🔌 API Endpoints Overview

### Authentication

- `POST /api/auth/register` - Register a new user
- `PUT /api/auth/login` - Login (Returns JWT in Cookie)

### Products

- `GET /api/products/all` - List all products
- `GET /api/products/{id}` - Get product details
- `POST /api/products/save` - Create a product
- `GET /api/products/by-category?category={name}` - Filter by category

### Users

- `PUT /api/users/updateName/{email}` - Update full name
- `PUT /api/users/updatePassword/{email}` - Change password

_Note: This is a summary. Explore the controllers in `src/main/java/com/larr/app/e_commerce/controller` for all available endpoints._

## 🤝 Contributing

Contributions are welcome! Please fork the repository and create a pull request.
