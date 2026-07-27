# Ghi chú chỉnh Class Diagram theo UML

## Vì sao cần chỉnh

Hình mẫu bạn gửi là class diagram kiểu UML chuẩn hơn: mỗi class có ngăn tên class, ngăn thuộc tính và ngăn phương thức. Ngoài ra diagram còn thể hiện các quan hệ như kế thừa, kết hợp, phụ thuộc hoặc composition. Bản `CLS-00-simple-overall-preview` trước đó giống domain/table overview hơn, phù hợp để nhìn tổng thể entity nhưng chưa đúng tinh thần class diagram nghiệp vụ.

## File UML-style đã tạo

- `docs/diagrams/mermaid/CLS-UML-01-user-catalog-order.mmd`
- `docs/diagrams/mermaid/CLS-UML-02-booking-payment-refund.mmd`
- `docs/diagrams/mermaid/CLS-UML-03-inventory.mmd`

## Quy ước trong diagram

- Entity/domain class dùng thuộc tính private, ký hiệu `-`.
- Service class dùng stereotype `<<service>>` và phương thức public, ký hiệu `+`.
- Không vẽ getter/setter vì source dùng Lombok và các method này không phải nghiệp vụ.
- Không vẽ kế thừa nếu source code không có quan hệ extends rõ ràng giữa entity.
- Dùng `*--` cho composition, ví dụ `Order` chứa `OrderItem`.
- Dùng `o--` cho aggregation, ví dụ `User` có nhiều `ShippingAddress`.
- Dùng `..>` cho dependency, ví dụ `OrderService` phụ thuộc vào `Order`.

## Đoạn đưa vào báo cáo

Class diagram được chia theo module lớn để dễ đọc trên khổ A4. Mỗi class entity thể hiện các thuộc tính nghiệp vụ chính, trong khi các service class thể hiện các phương thức xử lý nghiệp vụ quan trọng. Diagram không liệt kê getter/setter để tránh rối và tập trung vào luồng xử lý chính của hệ thống. Các quan hệ composition, aggregation và dependency giúp mô tả rõ đối tượng nào sở hữu dữ liệu, đối tượng nào chỉ sử dụng hoặc thao tác trên đối tượng khác. Cách chia này phù hợp hơn với UML class diagram so với việc vẽ toàn bộ bảng vào một hình duy nhất.
