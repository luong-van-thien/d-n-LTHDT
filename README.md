# ĐỒ ÁN GIỮA KỲ: HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)

* **Môn học:** Lập trình hướng đối tượng (Java)
* **Giảng viên hướng dẫn:** ThS. Nguyễn Thị Tuyết Hải
* **Đơn vị:** Học viện Công nghệ Bưu chính Viễn thông (PTIT HCM)

---

## 👥 Bảng phân công nhiệm vụ và tỷ lệ đóng góp (Nhóm 4 người)

| STT | Họ và Tên | MSSV | Vai trò | Nhiệm vụ đảm nhiệm | File code phụ trách | Tỷ lệ đóng góp | Đánh giá |
| :---: | :--- | :---: | :---: | :--- | :--- | :---: | :---: |
| **1** | **Lương Văn Thiện** | `N24DCCN076` | Nhóm trưởng | Thiết kế các lớp OOP, kế thừa, đa hình cho sản phẩm và khách hàng | `Product.java`, `FoodProduct.java`, `BeverageProduct.java`, `ElectronicProduct.java`, `Customer.java`, `RegularCustomer.java`, `VIPCustomer.java`, `Category.java`, `Discountable.java` | 25% | Hoàn thành tốt |
| **2** | **Trần Nhật Nguyên** | `N22DCCN057` | Thành viên | Làm giao diện Quản lý kho, bảng dữ liệu, chức năng tìm kiếm và sắp xếp | `ProductPanel.java`, `ProductController.java`, `ProductService.java` | 25% | Hoàn thành tốt |
| **3** | **Nguyễn Hải Phong** | `N24DCCN059` | Thành viên | Làm giao diện Bán hàng (POS), giỏ hàng, tính tiền, giảm giá và in hóa đơn | `PosPanel.java`, `PosController.java`, `OrderService.java`, `Invoice.java`, `InvoiceItem.java`, `Exportable.java` | 25% | Hoàn thành tốt |
| **4** | **Ngô Đức Thành** | `N24DCCN074` | Thành viên | Xử lý đọc/ghi file CSV, các lớp Exception, giao diện Thống kê và Cảnh báo | `ProductRepository.java`, `CustomerRepository.java`, `InvoiceRepository.java`, `StatisticPanel.java`, `StatisticService.java`, `*Exception.java` | 25% | Hoàn thành tốt |

> **Tổng tỷ lệ đóng góp:** 100% (Mỗi thành viên đóng góp 25%).

---

## 📌 1. Giới thiệu đề tài

Đề tài xây dựng một phần mềm quản lý bán hàng và kho mini bằng ngôn ngữ **Java**, sử dụng giao diện đồ họa **Java Swing** và lưu trữ dữ liệu bằng **file CSV**.

Hệ thống giúp quản lý các loại sản phẩm, quản lý thông tin khách hàng, hỗ trợ thu ngân bán hàng, tính tiền, xuất hóa đơn ra file text và theo dõi doanh thu, cảnh báo hàng sắp hết hoặc sắp hết hạn.

---

## 💡 2. Các kiến thức OOP áp dụng trong đồ án

Dự án áp dụng đầy đủ các kiến thức đã học trong môn Lập trình hướng đối tượng:

1. **Đóng gói (Encapsulation):**
   * Các thuộc tính của lớp đều để `private`/`protected`.
   * Sử dụng các hàm `getter`/`setter` và kiểm tra tính hợp lệ dữ liệu (không cho nhập giá hoặc số lượng âm).

2. **Kế thừa (Inheritance):**
   * Lớp cha `Product` $\rightarrow$ các lớp con: `FoodProduct` (thực phẩm có HSD), `BeverageProduct` (đồ uống), `ElectronicProduct` (đồ điện tử có bảo hành).
   * Lớp cha `Customer` $\rightarrow$ các lớp con: `RegularCustomer` (khách thường), `VIPCustomer` (khách VIP).

3. **Đa hình (Polymorphism):**
   * Phương thức tính lợi nhuận `calculateProfit()` được viết đè ở từng loại sản phẩm khác nhau.
   * Phương thức tính giảm giá `calculateDiscount()` tự động áp dụng mức giảm giá khác nhau cho từng loại khách hàng.
   * Ghi đè các phương thức `toString()`, `equals()`, `hashCode()` của lớp `Object`.

4. **Interface & Abstract Class:**
   * Lớp trừu tượng: `Product`, `Customer`.
   * Interface: `Discountable` (giảm giá), `Exportable` (xuất file hóa đơn).
   * Cài đặt `Comparable<Product>` và dùng `Comparator` kết hợp **Lambda Expression** để sắp xếp sản phẩm theo tên, giá, số lượng tồn kho.

5. **Xử lý ngoại lệ (Exception Handling):**
   * Tự định nghĩa các Exception: `DuplicateIdException`, `OutOfStockException`, `InvalidDataException`, `NotFoundException`.
   * Sử dụng `try-catch` để bắt lỗi khi nhập liệu, không làm ứng dụng bị dừng đột ngột.

6. **Java Collections Framework:**
   * `ArrayList`: lưu danh sách sản phẩm, khách hàng, giỏ hàng.
   * `HashMap`: tra cứu nhanh sản phẩm, khách hàng theo mã ID.
   * `HashSet`: kiểm tra trùng mã ID.

7. **Lập trình giao diện Java Swing & Mô hình MVC:**
   * Sử dụng `JFrame`, `JTabbedPane`, `JTable`, `JTextField`, `JComboBox`, `JOptionPane`...
   * Tách rõ các phần: Model (dữ liệu), View (giao diện), Controller (xử lý sự kiện).

8. **Lưu trữ dữ liệu (File I/O):**
   * Đọc và ghi dữ liệu ra các file `products.csv`, `customers.csv`, `invoices.csv`.
   * Xuất hóa đơn bán lẻ ra file text `.txt`.

---

## 🖥️ 3. Các chức năng chính

Phần mềm gồm 4 tab chức năng:
* **Tab 1 - Quản lý kho & Sản phẩm:** Hiển thị danh sách sản phẩm trên bảng, thêm, sửa, xóa, tìm kiếm, sắp xếp và lọc sản phẩm sắp hết hàng / sắp hết hạn.
* **Tab 2 - Quầy thu ngân (POS):** Chọn sản phẩm đưa vào giỏ hàng, chọn loại khách hàng để tính giảm giá, tính tiền thừa và in hóa đơn bán lẻ.
* **Tab 3 - Lịch sử hóa đơn:** Xem lại các hóa đơn đã thanh toán và có nút mở file hóa đơn text đã in.
* **Tab 4 - Thống kê & Cảnh báo:** Thống kê tổng doanh thu, lợi nhuận ước tính, số lượng hóa đơn, tổng tồn kho và bảng cảnh báo các mặt hàng cần nhập thêm hoặc thanh lý.

---

## 🚀 4. Hướng dẫn chạy chương trình

* **Cách 1 (Dùng VS Code):** Mở thư mục `MiniMartSystem` trong VS Code, mở file `MainApp.java` rồi nhấn phím **F5** (hoặc nút Run ▶️).
* **Cách 2 (Chạy trực tiếp):** Nhấp đúp chuột vào file **`run.bat`** trong thư mục để mở ứng dụng.
* **Biên dịch code:** Nhấp đúp chuột vào file **`compile.bat`**.
