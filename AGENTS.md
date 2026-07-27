# Quy tắc dự án

## Giao tiếp

- Trả lời người dùng bằng tiếng Việt, trừ khi người dùng yêu cầu ngôn ngữ khác.
- Giải thích ngắn gọn và đưa kết quả lên trước.
- Nêu rõ các giả định quan trọng trước khi thực hiện.

## Phạm vi và an toàn

- Không sửa tệp khi người dùng chỉ yêu cầu đọc, giải thích, review hoặc chẩn đoán.
- Chỉ thay đổi những gì cần thiết cho yêu cầu hiện tại; không tự ý refactor phần không liên quan.
- Giữ nguyên mọi thay đổi staged, unstaged và tệp chưa được Git theo dõi của người dùng.
- Không để lộ thông tin đăng nhập hoặc đưa secret từ cấu hình local vào câu trả lời.
- Không chạy lệnh Git hoặc lệnh hệ thống tệp có tính phá hủy khi chưa được cho phép rõ ràng.

## Cấu trúc dự án

- `backend/` là ứng dụng Java 21, Spring Boot 3 và được build bằng Maven.
- Các module backend nằm trong `hoang.shop`: `identity`, `categories`, `cart`, `order`, `common` và `config`.
- Giữ nguyên kiến trúc controller -> service -> repository hiện tại.
- Dùng DTO tại biên API và dùng MapStruct cho việc mapping ở những nơi dự án đang áp dụng.
- Mọi thay đổi cơ sở dữ liệu phải được thêm bằng migration Flyway mới trong `backend/src/main/resources/db/migration/`; không sửa lại migration đã áp dụng.
- `frontend/` sử dụng HTML, CSS và JavaScript module thuần. Không thêm framework hoặc build system nếu chưa được yêu cầu rõ ràng.
- Tập trung cấu hình URL API tại `frontend/assets/js/api/config.js`.

## Quy ước viết mã

- Tuân theo cách định dạng và đặt tên của mã nguồn xung quanh.
- Ưu tiên constructor injection và các mẫu Spring/Lombok đang có trong dự án.
- Validate request DTO và trả lỗi thông qua cơ chế xử lý exception hiện tại.
- Luôn kiểm tra quyền sở hữu đối với giỏ hàng, địa chỉ, đánh giá, phiên đăng nhập và đơn hàng của người dùng.
- Giữ endpoint public, endpoint cần đăng nhập và endpoint admin nhất quán với `SecurityConfig`.
- Tránh hard-code secret, địa chỉ host phụ thuộc môi trường, lệnh in debug và hằng số trùng lặp.
- Giữ cách gửi authentication header và `credentials` nhất quán giữa các request frontend.

## Kiểm tra

- Khi thay đổi backend, chạy test liên quan trực tiếp trước từ thư mục `backend/` bằng `./mvnw test` hoặc lệnh có `-Dtest=...`.
- Khi thay đổi frontend, kiểm tra trang bị ảnh hưởng, console trình duyệt, network request và các trạng thái loading, dữ liệu rỗng, lỗi, đã đăng nhập.
- Không khẳng định test đã thành công nếu chưa thực sự chạy test.
- Nếu không thể kiểm tra, nêu rõ nguyên nhân và phần nào vẫn chưa được xác minh.

## Tiết kiệm token

- Chỉ đọc các tệp liên quan đến yêu cầu hiện tại; dùng `rg` và đọc theo phạm vi thay vì tải toàn bộ repository.
- Tận dụng ngữ cảnh dự án đã biết và không đọc lại nhiều lần các tệp lớn không thay đổi.
- Tóm tắt kết quả thay vì chép nguyên tệp mã nguồn hoặc output dài của lệnh.
