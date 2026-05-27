# 📱 API Documentation - ShopBongDa

## 🛒 CART API

### 1. Tạo giỏ hàng (Create Cart)

**Endpoint:** `POST /api/public/carts`

**Headers:**

```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

**Response (201 Created):**

```json
{
  "code": 1000,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "totalAmount": 3,
    "totalPrice": 2500000.0,
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Bóng đá số 5",
        "totalQuantity": 2,
        "priceAtAddedTime": 500000.0,
        "totalPrice": 1000000.0
      },
      {
        "id": 2,
        "productId": 3,
        "productName": "Áo bóng đá",
        "totalQuantity": 1,
        "priceAtAddedTime": 1500000.0,
        "totalPrice": 1500000.0
      }
    ],
    "createdAt": "2025-12-04T12:00:00",
    "updatedAt": "2025-12-04T12:00:00"
  }
}
```

---

### 2. Lấy tất cả giỏ hàng (Get All Carts)

**Endpoint:** `GET /api/public/carts`

**Response:**

```json
{
  "code": 1000,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "totalAmount": 3,
      "totalPrice": 2500000.00,
      "items": [...],
      "createdAt": "2025-12-04T12:00:00",
      "updatedAt": "2025-12-04T12:00:00"
    }
  ]
}
```

---

### 3. Lấy giỏ hàng theo ID (Get Cart by ID)

**Endpoint:** `GET /api/public/carts/{id}`

**Example:** `GET /api/public/carts/1`

**Response:**

```json
{
  "code": 1000,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "totalAmount": 3,
    "totalPrice": 2500000.00,
    "items": [...],
    "createdAt": "2025-12-04T12:00:00",
    "updatedAt": "2025-12-04T12:00:00"
  }
}
```

---

### 4. Cập nhật giỏ hàng (Update Cart)

**Endpoint:** `PUT /api/public/carts/{id}`

**Example:** `PUT /api/public/carts/1`

**Request Body:**

```json
{
  "items": [
    {
      "productId": 2,
      "quantity": 3
    },
    {
      "productId": 4,
      "quantity": 1
    }
  ]
}
```

**Response:**

```json
{
  "code": 1000,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "totalAmount": 4,
    "totalPrice": 3000000.00,
    "items": [...],
    "updatedAt": "2025-12-04T12:30:00"
  }
}
```

---

### 5. Xóa giỏ hàng (Delete Cart)

**Endpoint:** `DELETE /api/public/carts/{id}`

**Example:** `DELETE /api/public/carts/1`

**Response:**

```json
{
  "code": 1000,
  "message": "Deleted Cart with id: 1",
  "data": null
}
```

---

## 🛍️ ORDER API

### 1. Tạo Order từ Cart (Checkout Cart) ⭐ MỚI

**Endpoint:** `POST /api/public/orders/checkout-cart`

**Headers:**

```
Authorization: Bearer <token>
```

**Query Parameters:**

- `cartId` (required): ID của giỏ hàng
- `shippingAddressId` (required): ID địa chỉ giao hàng

**Example:**

```
POST /api/public/orders/checkout-cart?cartId=1&shippingAddressId=5
```

**Response:**

```json
{
  "code": 1000,
  "message": "success",
  "data": {
    "id": 30,
    "userId": "1",
    "totalAmount": 2500000.0,
    "shippingCost": 0,
    "status": "Chờ xác nhận",
    "fullName": "Phan Kỳ Anh",
    "phone": "0354878763",
    "address": "132/8/6C Hẻm 132, Đường 3/2,...",
    "orderDate": "2025-12-04T12:35:00",
    "items": [
      {
        "id": 26,
        "productId": 1,
        "productName": "Bóng đá số 5",
        "quantity": 2,
        "price": 500000.0,
        "total": 1000000.0,
        "createdAt": "2025-12-04T12:35:00",
        "updatedAt": "2025-12-04T12:35:00"
      },
      {
        "id": 27,
        "productId": 3,
        "productName": "Áo bóng đá",
        "quantity": 1,
        "price": 1500000.0,
        "total": 1500000.0,
        "createdAt": "2025-12-04T12:35:00",
        "updatedAt": "2025-12-04T12:35:00"
      }
    ]
  }
}
```

---

### 2. Tạo Order trực tiếp (Create Order)

**Endpoint:** `POST /api/public/orders`

**Headers:**

```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "shippingAddressId": 5,
  "shippingCost": 10000
}
```

**Response:**

```json
{
  "code": 1000,
  "message": "success",
  "data": {
    "id": 31,
    "userId": "1",
    "totalAmount": 1010000.00,
    "shippingCost": 10000,
    "status": "Chờ xác nhận",
    "items": [...]
  }
}
```

---

### 3. Lấy tất cả Order (Get All Orders)

**Endpoint:** `GET /api/public/orders`

**Response:**

```json
{
  "code": 1000,
  "message": "success",
  "data": [
    {
      "id": 30,
      "userId": "1",
      "totalAmount": 2500000.00,
      "items": [...]
    }
  ]
}
```

---

### 4. Lấy Order theo ID (Get Order by ID)

**Endpoint:** `GET /api/public/orders/{id}`

**Example:** `GET /api/public/orders/30`

**Response:**

```json
{
  "code": 1000,
  "message": "success",
  "data": {
    "id": 30,
    "userId": "1",
    "totalAmount": 2500000.00,
    "items": [...]
  }
}
```

---

### 5. Cập nhật Order (Update Order)

**Endpoint:** `PUT /api/public/orders/{id}`

**Example:** `PUT /api/public/orders/30`

**Request Body:**

```json
{
  "items": [
    {
      "productId": 2,
      "quantity": 1
    }
  ],
  "shippingAddressId": 6
}
```

---

### 6. Cập nhật trạng thái Order (Update Order Status)

**Endpoint:** `PUT /api/public/orders/{id}/status`

**Example:** `PUT /api/public/orders/30/status?status=Đã thanh toán`

**Query Parameters:**

- `status`: Trạng thái mới (ví dụ: "Đã thanh toán", "Đang giao", "Đã giao")

---

### 7. Xóa Order (Delete Order)

**Endpoint:** `DELETE /api/public/orders/{id}`

**Example:** `DELETE /api/public/orders/30`

---

## 💳 PAYMENT API

### 1. Xác nhận thanh toán (Confirm Payment)

**Endpoint:** `PUT /api/public/payments/orders/{orderId}/confirm`

**Example:** `PUT /api/public/payments/orders/30/confirm`

**Response:**

```json
{
  "code": 1000,
  "message": "success",
  "data": {
    "id": 5,
    "type": "Giao hàng tận nơi",
    "status": "Đã thanh toán",
    "totalAmount": 2500000.0,
    "method": "Tiền mặt",
    "transactionCode": "PAY-ABC12345",
    "paymentDate": "2025-12-04T12:40:00",
    "createdAt": "2025-12-04T12:35:00",
    "updatedAt": "2025-12-04T12:40:00"
  }
}
```

---

### 2. Lấy Payment theo ID (Get Payment by ID)

**Endpoint:** `GET /api/public/payments/{id}`

**Example:** `GET /api/public/payments/5`

---

### 3. Lấy Payment theo Order ID (Get Payment by Order ID)

**Endpoint:** `GET /api/public/payments/orders/{orderId}`

**Example:** `GET /api/public/payments/orders/30`

---

### 4. Cập nhật trạng thái Payment (Update Payment Status)

**Endpoint:** `PUT /api/public/payments/{id}/status`

**Example:** `PUT /api/public/payments/5/status?value=Đã thanh toán`

---

## 🎯 QUY TRÌNH MTEST (CART -> ORDER -> PAYMENT)

### Quy trình hoàn chỉnh:

**Bước 1: Tạo giỏ hàng**

```bash
POST /api/public/carts
Authorization: Bearer <token>

{
  "items": [
    {"productId": 1, "quantity": 2},
    {"productId": 3, "quantity": 1}
  ]
}
```

→ Nhận `cartId`: 1

---

**Bước 2: Checkout giỏ hàng thành Order** ⭐

```bash
POST /api/public/orders/checkout-cart?cartId=1&shippingAddressId=5
Authorization: Bearer <token>
```

→ Nhận `orderId`: 30, `paymentId`: 5
→ Giỏ hàng bị xóa tự động ✅

---

**Bước 3: Xác nhận thanh toán**

```bash
PUT /api/public/payments/orders/30/confirm
Authorization: Bearer <token>
```

→ Payment status: "Đã thanh toán" ✅
→ Order status: "Đã thanh toán" ✅

---

## ✅ TÍNH NĂNG ĐÃ HOÀN THÀNH

- ✅ Thêm sản phẩm vào giỏ hàng
- ✅ Xem chi tiết giỏ hàng (productId, productName, price, quantity)
- ✅ Cập nhật giỏ hàng
- ✅ **Checkout giỏ hàng thành Order** (MỚI)
- ✅ Xóa giỏ hàng tự động khi checkout
- ✅ Tạo Order từ Cart
- ✅ Xác nhận thanh toán

---

## 🔗 Liên kết Frontend

Frontend có thể:

1. Gọi API `POST /api/public/carts` để tạo giỏ hàng
2. Gọi API `GET /api/public/carts/{id}` để xem chi tiết giỏ hàng
3. Khi user click "Mua" → Gọi `POST /api/public/orders/checkout-cart?cartId=X&shippingAddressId=Y`
4. Khi user xác nhận thanh toán → Gọi `PUT /api/public/payments/orders/{orderId}/confirm`

---

## 🐛 Ghi chú

- Giỏ hàng sẽ bị xóa tự động khi checkout thành công
- Payment được tạo tự động khi Order được tạo
- Order status mặc định là "Chờ xác nhận"
- Payment status mặc định là "Chưa thanh toán"
