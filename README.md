# ĐỒ ÁN GIỮA KỲ: HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)

* **Môn học:** Lập trình hướng đối tượng (Java)
* **Giảng viên hướng dẫn:** ThS. Nguyễn Thị Tuyết Hải
* **Học viện Công nghệ Bưu chính Viễn thông (PTIT HCM)**

---

## 👥 BẢNG PHÂN CÔNG NHIỆM VỤ (NHÓM 4 NGƯỜI)

| STT | Họ và Tên | MSSV | Vai trò | Nhiệm vụ chính phụ trách | Tỷ lệ đóng góp |
| :---: | :--- | :---: | :---: | :--- | :---: |
| 1 | **Lương Văn Thiện** | `N24DCCN076` | Trưởng nhóm | Thiết kế kiến trúc OOP, Phân quyền RBAC (`User`, `AdminUser`, `CashierUser`) | **25%** |
| 2 | **Trần Nhật Nguyên** | `N22DCCN057` | Thành viên | Phân hệ Quản lý Kho, bảng dữ liệu & lọc tìm kiếm, tạo Phiếu nhập hàng | **25%** |
| 3 | **Nguyễn Hải Phong** | `N24DCCN059` | Thành viên | Phân hệ Bán hàng (POS), quét mã vạch Barcode, treo đơn, xuất hóa đơn | **25%** |
| 4 | **Ngô Đức Thành** | `N24DCCN074` | Thành viên | Phân hệ Lưu trữ File CSV, Validation Custom Exception & Dashboard Thống kê | **25%** |

> **Tổng tỷ lệ đóng góp:** 100% (Mỗi thành viên hoàn thành tốt 25% công việc).

---

## 🔐 PHÂN QUYỀN NGƯỜI DÙNG (ROLE-BASED ACCESS CONTROL - RBAC)

Hệ thống được bảo mật và phân chia vai trò rõ ràng ngay từ Màn hình Đăng nhập:
* **Tài khoản Quản lý (Admin):**
  * Toàn quyền truy cập cả 4 Tab.
  * Thêm, sửa, xóa sản phẩm trong kho, tạo Phiếu Nhập Hàng, xuất báo cáo CSV.
  * Xem Dashboard Báo cáo Doanh thu, Lợi nhuận và phân tích rủi ro tài chính.
* **Tài khoản Thu ngân (Cashier):**
  * Chỉ được thao tác tại Tab 2 (Quầy POS) và Tab 3 (Lịch sử Hóa đơn).
  * Tab 1 (Kho hàng) chuyển sang chế độ **Chỉ đọc (Read-only)** để tra cứu tồn kho, bị khóa hoàn toàn các nút Thêm/Sửa/Xóa.
  * Tab 4 (Thống kê Doanh thu) bị **khóa / vô hiệu hóa** để bảo mật bí mật kinh doanh của cửa hàng.

### 🔑 Danh sách tài khoản thử nghiệm nhanh (1-Click Login):
| Tên đăng nhập | Mật khẩu | Họ và tên | Vai trò | Quyền hạn |
| :--- | :--- | :--- | :--- | :--- |
| `admin` | `admin123` | Lương Văn Thiện | Quản lý (Admin) | Toàn quyền 4 Tab, sửa kho & xem doanh thu |
| `cashier` | `cashier123` | Nhân viên Thu Ngân | Thu ngân (Cashier) | Bán hàng POS, chỉ đọc kho & khóa tab thống kê |
| `ptit` | `123456` | ThS. Nguyễn Thị Tuyết Hải | Giảng viên hướng dẫn | Toàn quyền kiểm tra và đánh giá đồ án |

---

## 💡 CÁC KIẾN THỨC OOP ÁP DỤNG TRONG ĐỒ ÁN

1. **Đóng gói (Encapsulation):**
   * Các thuộc tính để `private`/`protected`, truy xuất qua `getter/setter`.
   * Kiểm soát tính toàn vẹn dữ liệu: chặn nhập chữ vào ô số, chặn số âm, chặn giá bán thấp hơn giá nhập, chặn HSD trước NSX.
2. **Kế thừa (Inheritance):** 
   * `Product` $\rightarrow$ `FoodProduct` (NSX, HSD), `BeverageProduct` (thể tích, độ cồn), `ElectronicProduct` (bảo hành, công suất).
   * `Customer` $\rightarrow$ `RegularCustomer` (khách thường), `VIPCustomer` (khách VIP).
   * `User` $\rightarrow$ `AdminUser` (quản lý), `CashierUser` (thu ngân).
3. **Đa hình (Polymorphism):**
   * Kiểm tra quyền người dùng `canAccessTab()`, `canModifyProduct()`, `canViewStatistics()` đa hình theo từng vai trò tài khoản.
   * Tính lợi nhuận `calculateProfit()` viết đè theo đặc thù từng ngành hàng thực tế:
     * `FoodProduct`: Trừ 5% phí hao hụt & bảo quản lạnh; giảm giá 20% xả hàng nếu cận date.
     * `BeverageProduct`: Trừ 3% phí vận chuyển/kho cồng kềnh; trích 10% thuế tiêu thụ đặc biệt nếu là đồ uống có cồn.
     * `ElectronicProduct`: Trích 10% quỹ dự phòng rủi ro bảo hành và dịch vụ hậu mãi.
   * Tính giảm giá `calculateDiscount()` tự động áp dụng chiết khấu đa hình theo loại khách hàng (`RegularCustomer`, `VIPCustomer`).
   * Tự động nâng hạng khách hàng từ `Regular` lên `VIP` khi tích lũy mua sắm đạt $\ge$ 5.000.000 VNĐ.
   * Ghi đè `toString()`, `equals()`, `hashCode()` của lớp `Object`.
4. **Interface & Abstract Class:**
   * Lớp trừu tượng: `Product`, `Customer`, `User`.
   * Interface: `Discountable` (giảm giá), `Exportable` (xuất file hóa đơn/phiếu nhập).
   * Sắp xếp bằng `Comparable<Product>` và `Comparator` kết hợp **Lambda Expression**.
5. **Xử lý ngoại lệ (Custom Exception Handling):**
   * Tạo các Exception riêng: `DuplicateIdException`, `OutOfStockException`, `InvalidDataException`, `NotFoundException`.
   * Toàn bộ thao tác thêm sửa sản phẩm, thanh toán tiền đều được try-catch và hiển thị bằng `JOptionPane` tiếng Việt thân thiện, ứng dụng không bao giờ bị crash.
6. **Collections Framework:** Dùng `ArrayList` (danh sách), `HashMap` (tra cứu nhanh O(1) theo mã ID), `HashSet` (chặn trùng mã ID).
7. **Giao diện Java Swing & Mô hình MVC:** Giao diện gồm 4 Tab trực quan (`JTabbedPane`), chia rõ Model - View - Controller.
8. **Lưu trữ dữ liệu (File I/O):** Tự động lưu và đọc dữ liệu từ `products.csv`, `customers.csv`, `invoices.csv`, `users.csv`.
9. **Xử lý Edge Cases giao diện:**
   * Tên sản phẩm quá dài (ví dụ: *"Bia Heineken Silver lon 330ml thùng 24 lon Tết 2026"*): Không bị vỡ bố cục, hiển thị dấu `...` và tích hợp **Tooltip (`setToolTipText`)** hiển thị đầy đủ 100% nội dung khi rê chuột vào bất kỳ ô nào trên bảng.
   * Tìm kiếm thông minh Tiếng Việt: Tự động chuẩn hóa loại bỏ dấu tiếng Việt (gõ "m" hay "mi" đều tìm thấy "Mì tôm", "Mì Hảo Hảo", "Bánh mì").

---

## 🖥️ CÁC CHỨC NĂNG CHÍNH TRONG PHẦN MỀM

* **Màn hình Đăng nhập (Login):** Đăng nhập an toàn theo chuẩn RBAC, hỗ trợ 2 nút đăng nhập nhanh 1-Click cho Quản lý và Thu ngân.
* **Tab 1 - Quản lý Kho & Sản phẩm:** Hiển thị danh sách sản phẩm, thêm, sửa, xóa, tìm kiếm theo tên/mã, lọc theo danh mục, sắp xếp đa tiêu chí, cảnh báo tồn ít / cận date, tạo Phiếu Nhập Hàng tự động và xuất báo cáo kho CSV.
* **Tab 2 - Quầy thu ngân (POS):** Giả lập máy quét mã vạch (Barcode Scanner) với phím Enter và tiếng beep phần cứng, thêm nhanh 2-click, tăng giảm số lượng, lưu tạm/treo hóa đơn (Hold Bill), tính chiết khấu thành viên và in hóa đơn bán lẻ ra file text.
* **Tab 3 - Lịch sử hóa đơn:** Bảng Master - Detail xem lại toàn bộ các hóa đơn đã giao dịch cùng chi tiết từng mặt hàng đã mua, hỗ trợ mở trực tiếp file hóa đơn text và xuất báo cáo CSV.
* **Tab 4 - Thống kê & Cảnh báo:** Dashboard theo mốc thời gian (Hôm nay, 7 ngày qua, Tháng này, Tất cả), 4 thẻ KPI động, bảng thống kê doanh thu theo từng nhóm hàng, bảng phân tích rủi ro tài chính và phân loại hàng cận date/tồn thấp.

---

## 🚀 HƯỚNG DẪN CHẠY PHẦN MỀM

* **Cách 1 (Chạy nhanh):** Nhấp đúp chuột vào file **`run.bat`** trong thư mục `MiniMartSystem`.
* **Cách 2 (Trên VS Code):** Mở thư mục `MiniMartSystem` trên VS Code, mở file `MainApp.java` và bấm **F5** (Run).
* **Biên dịch lại code:** Nhấp đúp chuột vào file **`compile.bat`**.
