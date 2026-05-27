# Football Shop => done

## Description

Football shop is a fullstack e-commerce web application for selling football shop.
Users can browse products, add item to cart and place orders online.

---

## Features

### User Features

- Register & Login (JWT Authentication)
- Browse products with pagination
- View product details
- Add to cart
- Place orders
- Receive email notifications

### Admin Features

- Manage Users (CRUD)
- Manage Products, Categories, Brands
- Manage Orders
- Upload product images (Cloudinary)

## Tech Stack

### Frontend:

- ReactJS

### Backend:

- Spring Boot
- Spring Data JPA
- Spring Security
- Oauth2
- Spring validate
- JWT (Json Web Token)
- Redis (Caching, Token Blacklist)

### Database

- MySQL

### Others

- Cloudinary (image upload)
- Google Zxing (Qr code)
- Spring Mail (SMTP)

---

## Installation

### Clone project

```base
git clone https://github.com/KyAnhhh/shopbongda.git

### Run FrontEnd
cd frontend
yarn install
yarn start

### Run BackEnd
cd backend
mvn spring-boot:run

### Usage
- Register a new account
- Login to the system
- Browse products
- Add product to Cart
- Place Order

### Screenshots

(Add screenshots here: Home page, Product page, Cart, Admin dashboard)

### Challenges & Solutions
- Authentication & Authorization
  - Problem: secure API endpoints and manage user session
  - Solution: implemented JWT with Spring Security
- Image upload
  - Problem: store and manage product images
  - Solution: use Cloudinary to upload images
- Send email:
  - Problem: difficult in notification customers
  - Solution: use SMTP to send email customers when change order status
- Token management (logout)
  - Problem: can;t invalidate token when user log out
  - Solution: use Redis to save invalidated token

### Author
- Phan Ky Anh
- Github: http://github.com/KyAnhhh

```

```

```
