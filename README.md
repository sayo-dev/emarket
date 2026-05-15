# E-Market Backend

The backend service for the E-Market application, built with Spring Boot.

## API Documentation

The interactive API documentation is available via Swagger UI:
- [Swagger UI](https://injured-jeannette-subcuticular.ngrok-free.dev/swagger-ui/index.html)

## Features

- **Authentication**: JWT-based authentication with access and refresh tokens.
- **Product Management**: Browse and manage products, categories, and variants.
- **Cart & Orders**: Full cart operations and order processing flow.
- **Vendor Operations**: Specific endpoints for vendors to manage their products and orders.
- **Database Migrations**: Automated schema management using Flyway.
- **Image Upload**: Integration with Cloudinary for product image storage.
- **Mail Service**: SMTP integration for sending emails.

## Tech Stack

- **Framework**: Spring Boot
- **Language**: Java
- **Database**: PostgreSQL
- **Cache**: Redis
- **Migrations**: Flyway
- **Storage**: Cloudinary
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL
- Redis

### Configuration

The application uses environment variables for configuration. You can create a `.env` file in the root directory based on the `.env.example` file.

Key variables required:
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `MAIL_USERNAME`, `MAIL_PASSWORD`
- `JWT_SECRET_KEY`
- `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_SECRET_KEY`

### Running the Application

1. Clone the repository:
   ```bash
   git clone <repository_url>
   ```

2. Navigate to the project directory:
   ```bash
   cd e_market
   ```

3. Build the project:
   ```bash
   ./mvnw clean install
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Docker Support

A `docker-compose.yml` file is provided to spin up the required services like PostgreSQL and Redis.

To start the services:
```bash
docker-compose up -d
```
