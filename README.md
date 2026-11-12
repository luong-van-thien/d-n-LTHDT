# ĐỒ ÁN GIỮA KỲ: HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)

* **Môn học:** Lập trình hướng đối tượng (Java)
* **Giảng viên hướng dẫn:** ThS. Nguyễn Thị Tuyết Hải
* **Học viện Công nghệ Bưu chính Viễn thông (PTIT HCM)**

---

## 👥 BẢNG PHÂN CÔNG NHIỆM VỤ (NHÓM 4 NGƯỜI)

| STT | Họ và Tên | MSSV | Vai trò | Nhiệm vụ chính phụ trách | Tỷ lệ đóng góp |
| :---: | :--- | :---: | :---: | :--- | :---: |
| 1 | **Lương Văn Thiện** | `N24DCCN076` | Trưởng nhóm | Thiết kế kiến trúc OOP, các lớp Sản phẩm & Khách hàng | **25%** |
| 2 | **Trần Nhật Nguyên** | `N22DCCN057` | Thành viên | Phân hệ Quản lý Kho, bảng dữ liệu & lọc tìm kiếm | **25%** |
| 3 | **Nguyễn Hải Phong** | `N24DCCN059` | Thành viên | Phân hệ Bán hàng (POS), giỏ hàng & xuất hóa đơn | **25%** |
| 4 | **Ngô Đức Thành** | `N24DCCN074` | Thành viên | Phân hệ Lưu trữ File CSV, Exception & Thống kê | **25%** |

> **Tổng tỷ lệ đóng góp:** 100% (Mỗi thành viên hoàn thành tốt 25% công việc).

---

## 💡 CÁC KIẾN THỨC OOP ÁP DỤNG TRONG ĐỒ ÁN

1. **Đóng gói (Encapsulation):** Các thuộc tính để `private`/`protected`, truy xuất qua `getter/setter`, kiểm tra dữ liệu không cho nhập giá hoặc số lượng âm.
2. **Kế thừa (Inheritance):** 
   * `Product` $\rightarrow$ `FoodProduct` (HSD), `BeverageProduct` (thể tích, cồn), `ElectronicProduct` (bảo hành, công suất).
   * `Customer` $\rightarrow$ `RegularCustomer` (khách thường), `VIPCustomer` (khách VIP).
3. **Đa hình (Polymorphism):**
   * Tính lợi nhuận `calculateProfit()` viết đè theo đặc thù từng ngành hàng thực tế:
     * `FoodProduct`: Trừ 5% phí hao hụt & bảo quản lạnh; giảm giá 20% xả hàng nếu cận date.
     * `BeverageProduct`: Trừ 3% phí vận chuyển/kho cồng kềnh; trích 10% thuế tiêu thụ đặc biệt nếu là đồ uống có cồn.
     * `ElectronicProduct`: Trích 10% quỹ dự phòng rủi ro bảo hành và dịch vụ hậu mãi.
   * Tính giảm giá `calculateDiscount()` tự động áp dụng chiết khấu đa hình theo loại khách hàng (`RegularCustomer`, `VIPCustomer`).
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

## 🖥️ CÁC CHỨC NĂNG CHÍNH TRONG PHẦN MỀM

* **Tab 1 - Quản lý Kho & Sản phẩm:** Hiển thị danh sách sản phẩm, thêm, sửa, xóa, tìm kiếm, sắp xếp và lọc cảnh báo hàng sắp hết / sắp hết hạn.
* **Tab 2 - Quầy thu ngân (POS):** Chọn sản phẩm đưa vào giỏ hàng, chọn loại khách hàng để tính giảm giá, tính tiền thừa và in hóa đơn bán lẻ ra file text.
* **Tab 3 - Lịch sử hóa đơn:** Xem lại các hóa đơn đã thanh toán và có nút mở file hóa đơn text đã in.
* **Tab 4 - Thống kê & Cảnh báo:** Thống kê tổng doanh thu, lợi nhuận ước tính, số lượng hóa đơn, tổng tồn kho và cảnh báo các mặt hàng cần nhập thêm hoặc thanh lý.

---

## 🚀 HƯỚNG DẪN CHẠY PHẦN MỀM

* **Cách 1 (Chạy nhanh):** Nhấp đúp chuột vào file **`run.bat`** trong thư mục `MiniMartSystem`.
* **Cách 2 (Trên VS Code):** Mở thư mục `MiniMartSystem` trên VS Code, mở file `MainApp.java` và bấm **F5** (Run).
* **Biên dịch lại code:** Nhấp đúp chuột vào file **`compile.bat`**.
