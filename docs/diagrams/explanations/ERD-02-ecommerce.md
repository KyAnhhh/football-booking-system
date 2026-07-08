# ERD-02 E-commerce

## Mục đích

Sơ đồ này mô tả cụm dữ liệu bán hàng: catalog sản phẩm, biến thể, ảnh, giỏ hàng, đơn hàng, wishlist và review sản phẩm.

## Bảng/entity

- `CATEGORY`
- `BRAND`
- `PRODUCT`
- `SIZE`
- `PRODUCT_VARIANT`
- `PRODUCT_IMAGE`
- `CART`
- `CART_ITEM`
- `ORDERS`
- `ORDER_ITEM`
- `WISHLIST_ITEM`
- `REVIEW`
- `USER`, `SHIPPING_ADDRESS` được vẽ dạng rút gọn để thể hiện liên kết.

## Quan hệ chính

- `CATEGORY` và `BRAND` quản lý nhiều `PRODUCT`.
- Một `PRODUCT` có nhiều `PRODUCT_VARIANT` và `PRODUCT_IMAGE`.
- `SIZE` được gắn với `PRODUCT_VARIANT`.
- Một `USER` sở hữu một `CART`, nhiều `ORDERS`, nhiều `WISHLIST_ITEM` và nhiều `REVIEW`.
- `ORDERS` chứa nhiều `ORDER_ITEM`; `ORDER_ITEM` liên kết tới `PRODUCT_VARIANT`.

## Field đã rút gọn

Các field hiển thị tập trung vào tên, mô tả, trạng thái xuất bản, giá, tồn kho, số lượng, tổng tiền và trạng thái xử lý. Một số field snapshot trong `ORDER_ITEM` và `CART_ITEM` được giữ vì source code có lưu tên sản phẩm, size và giá tại thời điểm giao dịch.

## Đoạn đưa vào báo cáo

Cụm e-commerce tổ chức dữ liệu từ danh mục sản phẩm đến giỏ hàng và đơn hàng. Sản phẩm được tách thành biến thể theo size, giúp hệ thống quản lý giá, tồn kho và số lượng bán theo từng biến thể cụ thể. Khi người dùng thêm sản phẩm vào giỏ hàng hoặc đặt hàng, hệ thống lưu thông tin snapshot như tên sản phẩm, size và giá tại thời điểm thao tác để đảm bảo dữ liệu giao dịch ổn định. Wishlist cho phép người dùng lưu sản phẩm yêu thích, còn review được liên kết với sản phẩm và order item để kiểm soát quyền đánh giá sau khi mua.
