# ShopMart Ecommerce – Base Project (Microservice)

Base project cho bài kiểm tra Session 14. Gồm 3 service Spring Boot độc lập, mỗi service một database riêng (Database-per-service), **chưa** có hạ tầng Spring Cloud / Kafka / Redis.

## Công nghệ
Java 17 · Spring Boot 3.3.5 · Spring Data JPA · MySQL 8 · Lombok · Maven (multi-module).
BOM Spring Cloud `2023.0.3` đã khai báo sẵn ở `pom.xml` gốc → chỉ cần thêm starter, không cần ghi version.

## Cấu trúc
```
shopmart-base/
├── pom.xml                 # parent (aggregator)
├── docker-compose.yml      # MySQL 8
├── docker/mysql/init.sql   # tạo inventory_db, payment_db, order_db
├── postman/                # collection test API
├── inventory-service/      # :8082  – sản phẩm, tồn kho
├── payment-service/        # :8083  – ví, thanh toán, hoàn tiền
└── order-service/          # :8081  – đơn hàng
```
Mỗi service: `entity / repository / dto / service / controller / exception / config`, có `GlobalExceptionHandler`, log SLF4J, dữ liệu mẫu và Unit Test (Mockito).

## Chạy
```bash
docker compose up -d          # MySQL root/root tại localhost:3306
mvn clean install             # build + chạy test
```
Mở thư mục gốc bằng IntelliJ (Open → `pom.xml` gốc) rồi chạy 3 class `*ServiceApplication`.
Đổi thông tin DB bằng biến môi trường `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`.

## Port dự kiến
| Thành phần | Port |
|---|---|
| api-gateway (cần làm) | 8080 |
| order-service | 8081 |
| inventory-service | 8082 (instance 2: 8092) |
| payment-service | 8083 |
| eureka-server (cần làm) | 8761 |
| config-server (cần làm) | 8888 |

## API có sẵn

**inventory-service** – `/api/inventory/products`
| Method | Path | Mô tả |
|---|---|---|
| GET | `/api/inventory/products` | Danh sách sản phẩm |
| GET | `/api/inventory/products/{id}` | Chi tiết |
| POST | `/api/inventory/products` | Tạo `{name, price, stock}` |
| PUT | `/api/inventory/products/{id}` | Cập nhật |
| DELETE | `/api/inventory/products/{id}` | Xóa |
| PUT | `/api/inventory/products/{id}/deduct` | Trừ kho `{quantity}` |
| PUT | `/api/inventory/products/{id}/restore` | Hoàn kho `{quantity}` |

**payment-service** – `/api/payment`
| Method | Path | Mô tả |
|---|---|---|
| POST | `/api/payment/pay` | Thanh toán `{orderId, userId, amount}` |
| POST | `/api/payment/refund/{orderId}` | Hoàn tiền |
| GET | `/api/payment/order/{orderId}` | Lịch sử thanh toán của đơn |
| GET | `/api/payment/wallets/{userId}` | Số dư ví |

**order-service** – `/api/order`
| Method | Path | Mô tả |
|---|---|---|
| POST | `/api/order` | Tạo đơn `{userId, productId, quantity}` → `PENDING` |
| GET | `/api/order?userId=` | Danh sách đơn |
| GET | `/api/order/{id}` | Chi tiết |
| PUT | `/api/order/{id}/status` | Đổi trạng thái `{status, note}` |

## Dữ liệu mẫu
- Sản phẩm: iPhone 15 (22tr, tồn 10), Samsung Galaxy S24 (18tr, tồn 15), AirPods Pro (5.5tr, tồn 30)
- Ví: user 1 = 100.000.000, user 2 = 5.000.000 (dùng user 2 để tạo tình huống thanh toán thất bại)

## Phần sinh viên cần làm (xem `// TODO` trong code)
1. `config-server`, `eureka-server`, `api-gateway` + đăng ký 3 service.
2. FeignClient `inventory-service` trong order-service + LoadBalancer + Resilience4j.
3. Kafka (docker-compose) + Saga, compensating qua `/restore` và `/refund`.
4. Redis cache cho `ProductService` (`@Cacheable/@CachePut/@CacheEvict`).

> Lưu ý: sau khi clone, xóa thư mục `.git` và khởi tạo repo mới theo tên `[Tên lớp]_[Họ Tên]`.
