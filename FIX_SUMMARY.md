# 🔧 FIX SUMMARY - BUY NOW & CHECKOUT

## ✅ Vấn đề đã fix

### 1️⃣ **BUY_NOW Mode không hoạt động**

**Vấn đề:** Khi click "Mua ngay" ở ProductDetail, app redirect tới checkout nhưng Checkout.js không sử dụng product data từ location.state, nó luôn cố load từ giỏ hàng.

**Giải pháp:**

- Cập nhật Checkout.js để kiểm tra `location.state.mode`
- Nếu mode = "BUY_NOW", load product từ location.state
- Nếu mode = "CART_CHECKOUT", load từ giỏ hàng

### 2️⃣ **Lỗi routing: "checkout" → {id}**

**Vấn đề:** Endpoint `/api/public/orders/checkout` bị match vào path parameter `{id}` của `/api/public/orders/{id}`. Spring cố convert "checkout" thành Long và fail.

**Giải pháp:**

- Di chuyển `@PutMapping("/api/public/orders/checkout")` **lên trước** `@PutMapping("/api/public/orders/{id}")`
- Spring match endpoints từ trên xuống, nên specific paths phải đứng trước generic paths

### 3️⃣ **OrderRequest không nhận shippingCost=null**

**Vấn đề:** BUY_NOW gửi `shippingCost: 20000-40000`, nhưng OrderRequest có `@NotNull shippingCost`.

**Giải pháp:**

- Bỏ `@NotNull` khỏi `shippingCost`
- Backend tự động set mặc định là `BigDecimal.ZERO` nếu null

### 4️⃣ **PaymentMethod không được xử lý**

**Vấn đề:** Frontend gửi `paymentMethod` nhưng OrderRequest không có field này, backend hardcode "Tiền mặt".

**Giải pháp:**

- Thêm field `paymentMethod` vào OrderRequest
- Update OrderService.handleCreateOrder() để sử dụng paymentMethod từ request

---

## 🔄 Flow Hoạt động

### **BUY_NOW Flow:**

```
ProductDetail.js
  └─ Click "Mua ngay"
     └─ addToCart(true)
        └─ navigate("/cart/checkout", {
             state: {
               mode: "BUY_NOW",
               product: { productId, productVariantId, ... }
             }
           })

Checkout.js
  └─ useEffect detect mode = "BUY_NOW"
     └─ setCartItems([{ id: "temp_xxx", ...buyNowProduct }])
        └─ setSelectedItemIds(["temp_xxx"])

Click "Đặt hàng"
  └─ handleCheckout()
     └─ createOrder({
          shippingAddressId: xxx,
          paymentMethod: "COD",
          items: [{ productVariantId: xxx, quantity: 1 }],
          shippingCost: 20000
        })
```

### **CART_CHECKOUT Flow:**

```
Cart.js
  └─ Click "Đặt hàng"
     └─ navigate("/cart/checkout", {
          state: { mode: "CART_CHECKOUT" }
        })

Checkout.js
  └─ useEffect detect mode = "CART_CHECKOUT"
     └─ getCartByUser()
        └─ setCartItems([...items from cart])

Click "Đặt hàng"
  └─ handleCheckout()
     └─ checkout({
          shippingAddressId: xxx,
          paymentMethod: "COD",
          cartItemIds: [id1, id2, ...]
        })
```

---

## 📋 Các file đã sửa

### **Frontend:**

- ✅ `front-end/src/pages/client/Checkout.js` - Xử lý BUY_NOW mode
- ✅ `front-end/src/services/orderService.js` - Đã có sẵn createOrder + checkout

### **Backend:**

- ✅ `back-end/src/main/java/.../controller/OrderController.java` - Sắp xếp endpoint
- ✅ `back-end/src/main/java/.../dto/request/OrderRequest.java` - Thêm paymentMethod, bỏ @NotNull shippingCost
- ✅ `back-end/src/main/java/.../service/OrderService.java` - Sử dụng paymentMethod từ request

---

## 🧪 Test Checklist

- [ ] Click "Mua ngay" → Redirect tới checkout với product info
- [ ] Checkout hiển thị đúng sản phẩm BUY_NOW
- [ ] Có thể chỉnh địa chỉ + phương thức thanh toán
- [ ] Click "Đặt hàng" (BUY_NOW) → Success (không lỗi Hibernate)
- [ ] Click "Đặt hàng" từ giỏ hàng → Success
- [ ] Giỏ hàng được xóa sản phẩm sau checkout thành công
- [ ] Đơn hàng xuất hiện trong "Đơn hàng của tôi"

---

## 🚨 Có thể gặp phải

**Nếu vẫn gặp lỗi Hibernate:**

- Kiểm tra ProductVariant, Size, Product có @ManyToOne lazy loading?
- Đảm bảo transaction scope bao phủ cả việc fetch relationships

**Nếu vẫn gặp lỗi routing:**

- Reload backend (Maven rebuild)
- Clear browser cache

**Nếu BUY_NOW không load product:**

- Kiểm tra ProductDetail.js có gửi đúng state không
- Console log location.state để debug
