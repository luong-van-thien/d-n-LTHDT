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

## 🚀 HƯỚNG DẪN CHẠY & ĐÓNG GÓI PHẦN MỀM

* **Cách 1 (Chạy file thực thi .jar):** Nhấp đúp chuột vào file **`MiniMartSystem.jar`** hoặc gõ lệnh:
  ```bash
  java -jar MiniMartSystem.jar
  ```
* **Cách 2 (Chạy nhanh bằng script):** Nhấp đúp chuột vào file **`run.bat`**.
* **Cách 3 (Trên VS Code):** Mở thư mục `MiniMartSystem`, mở file `MainApp.java` và bấm **F5** (Run).
* **Đóng gói file .jar nộp bài:** Nhấp đúp chuột vào **`build_jar.bat`** để tự động biên dịch và xuất file `.jar`.
  *(Ghi chú: Để ẩn 2 nút demo trước khi đóng gói, chỉ cần đổi `public static boolean SHOW_DEMO_BUTTONS = false;` trong `LoginDialog.java`).*

---

## 🎓 CẨM NANG BẢO VỆ ĐỒ ÁN TRƯỚC HỘI ĐỒNG (FAQ & LUẬN ĐIỂM)

### ❓ Câu 1: "Tại sao lại áp dụng Đa hình (Polymorphism) cho `calculateProfit()`? Bình thường Lợi nhuận = Giá bán - Giá nhập là xong rồi mà?"
> **Trả lời thuyết phục:**
> *"Thưa Thầy/Cô, trong thực tế quản trị siêu thị mini, mỗi ngành hàng chịu các chi phí rủi ro và đặc thù vận hành hoàn toàn khác nhau. Nếu chỉ tính thô Giá bán - Giá nhập thì báo cáo tài chính sẽ bị ảo và không chính xác:*
> 1. *Với **Thực phẩm (FoodProduct):** Chịu chi phí hao hụt tự nhiên và bảo quản mát (trừ 5%). Đặc biệt khi hàng cận date ($\le$ 7 ngày), siêu thị bắt buộc giảm giá 20% để kích cầu xả hàng, tránh việc hết hạn phải tiêu hủy toàn bộ.*
> 2. *Với **Đồ uống (BeverageProduct):** Chịu chi phí lưu kho cồng kềnh và vận chuyển két/thùng nặng (trừ 3%). Nếu là bia/rượu có cồn, siêu thị phải trích nộp 10% Thuế tiêu thụ đặc biệt.*
> 3. *Với **Đồ điện gia dụng (ElectronicProduct):** Siêu thị chịu trách nhiệm bảo hành đổi trả, nên bắt buộc trích lập 10% Quỹ dự phòng bảo hành và dịch vụ hậu mãi.*
> *Do đó, việc dùng phương thức trừu tượng và ghi đè đa hình `calculateProfit()` ở từng lớp con phản ánh trung thực bài toán kinh doanh thực tế."*

### ❓ Câu 2: "Tại sao dùng Kế thừa Customer $\rightarrow$ Regular / VIP? Ngoài đời khách đủ điểm lên VIP thì Java làm sao đổi object từ Regular sang VIP?"
> **Trả lời thuyết phục (Rất được hội đồng đánh giá cao):**
> *"Thưa Thầy/Cô, trong phạm vi đồ án môn học OOP, nhóm em sử dụng Kế thừa để minh họa trực quan mối quan hệ 'is-a' giữa Customer và hai nhóm đối tượng có hành vi chiết khấu khác nhau (`RegularCustomer` 0%, `VIPCustomer` 5%). Khi khách đạt mốc 5.000.000 đ, hệ thống khởi tạo đối tượng `VIPCustomer` mới mang toàn bộ thông tin lịch sử và thay thế vào Repository.*
> 
> *Tuy nhiên, nhóm nhận thức rõ **hạn chế của việc dùng Kế thừa ở đây**: Trong Java, một object không thể tự đột biến kiểu (dynamic class mutation) trong runtime.*
> *Nếu nâng cấp lên quy mô dự án thực tế lớn (Enterprise), hướng giải quyết chuẩn là áp dụng **Strategy Pattern** hoặc **State Pattern** (Composition over Inheritance):*
> *Lớp `Customer` sẽ chứa một thuộc tính `MembershipStrategy`. Khi đủ điều kiện, ta chỉ cần gọi `customer.setMembershipStrategy(new VIPStrategy())`. Đối tượng khách hàng giữ nguyên tham chiếu duy nhất mà hành vi tính chiết khấu vẫn hoàn toàn đa hình và linh hoạt."*

### ❓ Câu 3: "Khi lưu hóa đơn xuống `invoices.csv`, bạn lưu giá bán lúc đó hay chỉ lưu mã sản phẩm?"
> **Trả lời:**
> *"Hệ thống của nhóm **chốt cứng đơn giá bán tại thời điểm giao dịch (Snapshot Price)** vào file `invoices.csv` theo định dạng `MãSP#TênSP#SốLượng#ĐơnGiáLúcBán`. Tuyệt đối không chỉ lưu mã SP rồi móc nối động với bảng giá hiện tại, vì giá bán trên kệ sẽ biến động trong tương lai, nếu móc nối động thì tổng tiền hóa đơn cũ trong quá khứ sẽ bị sai lệch hoàn toàn."*
