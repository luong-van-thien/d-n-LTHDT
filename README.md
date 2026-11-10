# ĐỒ ÁN GIỮA KỲ: HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)

* **Môn học:** Lập trình hướng đối tượng (Java)
* **Giảng viên hướng dẫn:** ThS. Nguyễn Thị Tuyết Hải
* **Học viện Công nghệ Bưu chính Viễn thông (PTIT HCM)**

---

## 👥 THÀNH VIÊN NHÓM & PHÂN CÔNG CÔNG VIỆC

> 📊 **File đính kèm:** Nhóm đã tạo sẵn 2 file bảng phân công đẹp mắt trong thư mục:
> * 📗 File Excel: **[Bang_Phan_Cong_Nhiem_Vu.xlsx](Bang_Phan_Cong_Nhiem_Vu.xlsx)**
> * 📘 File Word: **[Bang_Phan_Cong_Nhiem_Vu.docx](Bang_Phan_Cong_Nhiem_Vu.docx)**

### Tóm tắt tỷ lệ đóng góp (Nhóm 4 người - Mỗi bạn 25%):

1. **Lương Văn Thiện (N24DCCN076) - Trưởng nhóm (25%)**
   * Thiết kế các lớp OOP, kế thừa, đa hình cho sản phẩm và khách hàng.
   * File phụ trách: `Product.java`, `FoodProduct.java`, `BeverageProduct.java`, `ElectronicProduct.java`, `Customer.java`, `VIPCustomer.java`...

2. **Trần Nhật Nguyên (N22DCCN057) - Thành viên (25%)**
   * Làm giao diện Quản lý kho, bảng dữ liệu, tìm kiếm và sắp xếp Lambda.
   * File phụ trách: `ProductPanel.java`, `ProductController.java`, `ProductService.java`.

3. **Nguyễn Hải Phong (N24DCCN059) - Thành viên (25%)**
   * Làm giao diện Bán hàng (POS), giỏ hàng, tính giảm giá và in hóa đơn text.
   * File phụ trách: `PosPanel.java`, `PosController.java`, `OrderService.java`, `Invoice.java`.

4. **Ngô Đức Thành (N24DCCN074) - Thành viên (25%)**
   * Xử lý đọc/ghi file CSV, các lớp Exception, màn hình Thống kê doanh thu.
   * File phụ trách: `ProductRepository.java`, `CustomerRepository.java`, `StatisticPanel.java`, `*Exception.java`.

---

## 💡 CÁC KIẾN THỨC OOP ĐÃ ÁP DỤNG TRONG ĐỒ ÁN

1. **Đóng gói (Encapsulation):** Các thuộc tính để `private`/`protected`, truy xuất qua `getter/setter`, kiểm tra dữ liệu không cho nhập giá hoặc số lượng âm.
2. **Kế thừa (Inheritance):** 
   * `Product` $\rightarrow$ `FoodProduct` (HSD), `BeverageProduct` (thể tích, cồn), `ElectronicProduct` (bảo hành, công suất).
   * `Customer` $\rightarrow$ `RegularCustomer` (khách thường), `VIPCustomer` (khách VIP).
3. **Đa hình (Polymorphism):**
   * Tính lợi nhuận `calculateProfit()` viết đè theo từng loại sản phẩm.
   * Tính giảm giá `calculateDiscount()` tự động áp dụng chiết khấu theo loại khách hàng.
   * Ghi đè `toString()`, `equals()`, `hashCode()` của lớp `Object`.
4. **Interface & Abstract Class:**
   * Lớp trừu tượng: `Product`, `Customer`.
   * Interface: `Discountable` (giảm giá), `Exportable` (xuất file hóa đơn).
   * Sắp xếp bằng `Comparable<Product>` và `Comparator` kết hợp **Lambda Expression**.
5. **Xử lý ngoại lệ (Exception Handling):**
   * Tạo các Exception riêng: `DuplicateIdException`, `OutOfStockException`, `InvalidDataException`, `NotFoundException`.
   * Bắt lỗi bằng `try-catch`, thông báo bằng `JOptionPane`.
6. **Collections Framework:** Dùng `ArrayList` (danh sách), `HashMap` (tra cứu nhanh O(1) theo mã ID), `HashSet` (chặn trùng mã ID).
7. **Giao diện Java Swing & Mô hình MVC:** Giao diện gồm 4 Tab trực quan (`JTabbedPane`), chia rõ Model - View - Controller.
8. **Lưu trữ dữ liệu (File I/O):** Tự động lưu và đọc dữ liệu từ `products.csv`, `customers.csv`, `invoices.csv`.

---

## 🚀 HƯỚNG DẪN CHẠY PHẦN MỀM

* **Cách 1 (Chạy nhanh):** Nhấp đúp chuột vào file **`run.bat`** trong thư mục `MiniMartSystem`.
* **Cách 2 (Trên VS Code):** Mở thư mục `MiniMartSystem` trên VS Code, mở file `MainApp.java` và bấm **F5** (Run).
* **Biên dịch lại code:** Nhấp đúp chuột vào file **`compile.bat`**.
