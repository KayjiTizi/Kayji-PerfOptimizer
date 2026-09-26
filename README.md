# Kayji-PerfOptimizer

Plugin Minecraft (Spigot) tạo **tải tính toán trên toàn bộ lõi CPU** của server: cứ mỗi giây lại nộp các tác vụ tính số nguyên tố chạy song song trên thread pool bằng số lõi CPU.

> **Tác giả:** Kayji · **Phiên bản:** 1.0 · **API:** 1.13 · **Java:** 8

## Nó làm gì?

Khi plugin được bật:

1. Tạo `ExecutorService` với số luồng = **số lõi CPU** (`Runtime.availableProcessors()`).
2. Mỗi **20 tick (≈1 giây)** nộp `cores` tác vụ `heavyComputation()`.
3. Mỗi tác vụ **tính số nguyên tố liên tục trong 100 ms** rồi dừng (busy-loop 100 ms).

Khi tắt plugin: dừng task định kỳ và `shutdown()` thread pool (chờ tối đa 1 giây).

## ⚠️ Lưu ý quan trọng

- `plugin.yml` mô tả "tự động tăng hiệu suất server" — **thực tế không phải vậy**: code là vòng lặp tính toán chiếm CPU, **không** tối ưu tick, không giảm lag.
- Hiệu ứng thật: CPU luôn bận → có thể **giảm TPS**, tăng nhiệt/điện năng tiêu thụ.
- Không có lệnh, không có config để cấu hình.
- Chỉ nên chạy khi bạn muốn **test tải CPU / benchmark** server.

## Bảng lệnh

Plugin **không có lệnh** và **không có cấu hình** — bật/tắt bằng cách add/remove plugin rồi restart.

| Lệnh | Quyền | Mô tả |
| --- | --- | --- |
| — | — | Không có lệnh; hành vi tự chạy ngay khi `onEnable()` |

## Cài đặt

```bash
mvn package
```

Copy `target/PerfOptimizer-1.0.jar` vào thư mục `plugins/` rồi restart server.

## Cấu trúc dự án

```
├── pom.xml                                          Maven (Java 8, spigot-api 1.16.1)
└── src
    ├── main
    │   ├── java/com/myplugin/perfoptimizer
    │   │   └── PerfOptimizer.java                   onEnable → startBoost(), onDisable → stopBoost()
    │   └── resources
    │       └── plugin.yml                           Metadata (không có lệnh)
    └── test/java/.../AppTest.java                   Unit test mẫu (JUnit 4)
```

## Giấy phép

[GNU General Public License v3.0](LICENSE)
