# Bộ sơ đồ báo cáo

Các sơ đồ được rút gọn từ controller, service, repository, entity/domain, DTO,
mapper, enum và annotation JPA của project. Getter/setter và chi tiết triển khai
không cần thiết cho báo cáo đã được lược bỏ.

| File | Mục đích |
|---|---|
| `01-package-diagram.mmd` | Tổng quan kiến trúc frontend, backend và các module |
| `02-use-case-diagram.mmd` | Chức năng chính theo khách, khách hàng và quản trị |
| `03-booking-class-diagram.mmd` | Lớp cốt lõi của booking, sân và dịch vụ thêm |
| `04-ecommerce-class-diagram.mmd` | Catalog, biến thể, giỏ hàng và đánh giá |
| `05-pricing-class-diagram.mmd` | Luật giá ngày thường, ngày lễ và dịch vụ tính giá |
| `06-maintenance-class-diagram.mmd` | Bảo trì sân và kiểm tra trùng thời gian |
| `07-payment-order-class-diagram.mmd` | Đơn hàng, dòng hàng, địa chỉ và thanh toán |
| `08-booking-sequence-diagram.mmd` | Luồng khách hàng tạo booking |
| `09-confirm-booking-sequence-diagram.mmd` | Luồng admin xác nhận booking |
| `10-pricing-calculation-sequence-diagram.mmd` | Thứ tự ưu tiên và cộng giá từng slot |
| `11-booking-activity-diagram.mmd` | Vòng đời booking từ chọn sân đến hoàn tất/hủy |
| `12-erd-diagram.mmd` | ERD rút gọn, chia nhóm nghiệp vụ để dễ trình bày |

## Import vào Draw.io

1. Mở file `.mmd` và sao chép toàn bộ nội dung.
2. Trong Draw.io chọn **Arrange → Insert → Advanced → Mermaid**.
3. Dán nội dung, chọn kiểu chèn phù hợp và xác nhận.

Mermaid không có use-case UML chuẩn và không hỗ trợ chia `erDiagram` thành
subgraph. Vì vậy use-case và ERD dùng `flowchart` có nhóm, giúp bố cục gọn và
import ổn định hơn. Có thể đổi actor thành biểu tượng người hoặc tinh chỉnh đường
nối trực tiếp trong Draw.io nếu mẫu báo cáo yêu cầu UML hình thức.

Trong ERD, đường nét đứt `REF` biểu diễn liên kết logic qua trường định danh
(ví dụ `extra_services.inventory_item_id`), không khẳng định đó là association
JPA. Các quan hệ còn lại được suy ra từ annotation JPA và khóa ngoại quan trọng.

## Export để đưa vào Word

Trong Draw.io chọn **File → Export as → PNG/SVG/PDF**. Nên dùng **SVG** để hình
vẫn sắc nét khi phóng to; dùng **PNG** nếu phiên bản Word hoặc quy trình nộp bài
không xử lý SVG ổn định. Với ERD, nên dùng khổ ngang và có thể chỉnh nhẹ vị trí
các nhóm sau khi import để phù hợp chiều rộng trang.

