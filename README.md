# BÁO CÁO ĐỒ ÁN MÔN HỌC: HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART SYSTEM)

* **Môn học:** Lập trình hướng đối tượng (Java)
* **Giảng viên hướng dẫn:** ThS. Nguyễn Thị Tuyết Hải
* **Đơn vị đào tạo:** Học viện Công nghệ Bưu chính Viễn thông - Cơ sở tại TP. Hồ Chí Minh

---

## 👥 BẢNG PHÂN CÔNG NHIỆM VỤ THÀNH VIÊN (NHÓM 4 SINH VIÊN)

| STT | Họ và Tên | MSSV | Vai trò | Nhiệm vụ chính phụ trách | Tỷ lệ đóng góp |
| :---: | :--- | :---: | :---: | :--- | :---: |
| 1 | **Lương Văn Thiện** | `N24DCCN076` | Trưởng nhóm | Thiết kế kiến trúc MVC, phân quyền người dùng RBAC (`User`, `AdminUser`, `CashierUser`), tích hợp giao diện Swing | **25%** |
| 2 | **Trần Nhật Nguyên** | `N22DCCN057` | Thành viên | Phân hệ Quản lý Kho, đa hình `calculateProfit()`, lọc danh mục, cảnh báo date và lập phiếu đề xuất nhập hàng | **25%** |
| 3 | **Nguyễn Hải Phong** | `N24DCCN059` | Thành viên | Phân hệ Bán hàng (POS), hỗ trợ quét mã vạch Barcode, quản lý giỏ hàng, tính chiết khấu và lưu tạm đơn hàng | **25%** |
| 4 | **Ngô Đức Thành** | `N24DCCN074` | Thành viên | Phân hệ Lịch sử hóa đơn, xuất báo cáo CSV UTF-8, ngoại lệ tùy chỉnh (Custom Exceptions) và bảng điều khiển thống kê | **25%** |

> **Tổng tỷ lệ đóng góp:** 100% (Mỗi thành viên hoàn thành 25% khối lượng công việc được phân công).

---

## 🔐 PHÂN QUYỀN NGƯỜI DÙNG (ROLE-BASED ACCESS CONTROL - RBAC)

Hệ thống thiết lập cơ chế kiểm soát quyền hạn ngay từ màn hình đăng nhập:
* **Tài khoản Quản lý (Admin):**
  * Có quyền truy cập đầy đủ cả 4 phân hệ của chương trình.
  * Thêm, sửa, xóa sản phẩm, điều chỉnh số lượng tồn kho, tạo phiếu đề xuất nhập hàng và xuất báo cáo kho hàng.
  * Xem bảng điều khiển thống kê doanh thu, lợi nhuận và phân tích cơ cấu hàng hóa.
* **Tài khoản Thu ngân (Cashier):**
  * Thao tác chính tại Quầy bán hàng (POS) và xem Lịch sử hóa đơn.
  * Phân hệ Quản lý Kho hàng chuyển sang chế độ **Chỉ đọc (Read-only)** để tra cứu tồn kho, các nút Thêm/Sửa/Xóa bị vô hiệu hóa.
  * Phân hệ Thống kê tài chính bị **khóa truy cập** nhằm bảo mật thông tin doanh số của cửa hàng.

### 🔑 Tài khoản kiểm thử hệ thống:
| Tên đăng nhập | Mật khẩu | Họ và tên | Vai trò | Quyền hạn chính |
| :--- | :--- | :--- | :--- | :--- |
| `admin` | `admin123` | Quản trị viên | Quản lý (Admin) | Toàn quyền thao tác trên 4 phân hệ (Kho, POS, Hóa đơn, Thống kê) |
| `cashier` | `cashier123` | Nhân viên Thu Ngân | Thu ngân (Cashier) | Thao tác bán hàng POS, xem lịch sử hóa đơn; khóa phân hệ kho và thống kê |

---

## 💡 CÁC KIẾN THỨC HƯỚNG ĐỐI TƯỢNG (OOP) ÁP DỤNG TRONG ĐỒ ÁN

1. **Đóng gói (Encapsulation):**
   * Các thuộc tính của đối tượng được khai báo `private`/`protected`, truy xuất và thay đổi thông qua các phương thức `getter/setter`.
   * Kiểm soát tính toàn vẹn dữ liệu: kiểm tra rỗng, kiểm tra số âm, ràng buộc giá bán không nhỏ hơn giá nhập, ràng buộc hạn sử dụng sau ngày sản xuất.
2. **Kế thừa (Inheritance):** 
   * `Product` $\rightarrow$ `FoodProduct` (ngày sản xuất, hạn sử dụng), `BeverageProduct` (thể tích, nồng độ cồn), `ElectronicProduct` (thời gian bảo hành, công suất).
   * `Customer` $\rightarrow$ `RegularCustomer` (khách hàng thường), `VIPCustomer` (khách hàng VIP).
   * `User` $\rightarrow$ `AdminUser` (quản lý), `CashierUser` (thu ngân).
3. **Đa hình (Polymorphism):**
   * Phương thức `canAccessTab()`, `canModifyProduct()`, `canViewStatistics()` được ghi đè tại `AdminUser` và `CashierUser` để kiểm soát quyền truy cập linh hoạt theo từng vai trò.
   * Phương thức `calculateProfit()` được định nghĩa trừu tượng tại `Product` và ghi đè tại từng lớp con theo đặc thù chi phí của từng ngành hàng:
     * `FoodProduct`: Trừ 5% chi phí hao hụt và điện bảo quản lạnh; giảm giá 20% khi hàng cận hạn sử dụng ($\le$ 7 ngày).
     * `BeverageProduct`: Trừ 3% chi phí vận chuyển và lưu kho hàng lỏng cồng kềnh; trích nộp 10% thuế tiêu thụ đặc biệt nếu là đồ uống có cồn.
     * `ElectronicProduct`: Trích lập quỹ dự phòng bảo hành và dịch vụ sau bán hàng (10% nếu bảo hành $\ge$ 12 tháng, 5% nếu dưới 12 tháng).
   * Phương thức tính chiết khấu `calculateDiscount()` tự động áp dụng mức giảm giá theo hạng khách hàng.
   * Tự động nâng hạng khách hàng từ `RegularCustomer` lên `VIPCustomer` khi chi tiêu tích lũy đạt mốc quy định.
   * Ghi đè các phương thức chuẩn của lớp `Object`: `toString()`, `equals()`, `hashCode()`.
4. **Lớp trừu tượng (Abstract Class) & Giao diện (Interface):**
   * Lớp trừu tượng: `Product`, `Customer`, `User`.
   * Giao diện: `Discountable` (tính toán chiết khấu), `Exportable` (xuất dữ liệu hóa đơn/phiếu nhập ra tệp văn bản).
   * Hỗ trợ sắp xếp dữ liệu thông qua `Comparable<Product>` và `Comparator` kết hợp biểu thức Lambda.
5. **Xử lý ngoại lệ tùy chỉnh (Custom Exceptions):**
   * Xây dựng các lớp ngoại lệ riêng: `DuplicateIdException`, `OutOfStockException`, `InvalidDataException`, `NotFoundException`.
   * Các thao tác thêm, sửa sản phẩm, thanh toán đều được bao bọc trong khối `try-catch` và thông báo lỗi rõ ràng qua hộp thoại `JOptionPane`, đảm bảo chương trình không bị dừng đột ngột.
6. **Khung tập hợp (Collections Framework):**
   * `ArrayList`: Lưu danh sách sản phẩm, chi tiết giỏ hàng và lịch sử hóa đơn.
   * `HashMap`: Tra cứu nhanh theo mã định danh với thời gian O(1), phục vụ tìm kiếm tức thời khi quét mã vạch.
   * `HashSet`: Kiểm tra trùng lặp mã sản phẩm trong quá trình nhập liệu.
7. **Giao diện Java Swing & Mô hình MVC:**
   * Tổ chức mã nguồn phân tầng rõ ràng: Model (dữ liệu, xử lý nghiệp vụ), View (giao diện đồ họa), Controller (điều phối luồng sự kiện).
   * Cập nhật giao diện trên luồng sự kiện Event Dispatch Thread (EDT) bằng `SwingUtilities.invokeLater()` để bảo đảm an toàn luồng.
8. **Lưu trữ dữ liệu dạng tệp tin (File I/O):**
   * Tự động lưu và đọc dữ liệu từ các tệp `products.csv`, `customers.csv`, `invoices.csv`, `users.csv` theo bảng mã UTF-8.
9. **Xử lý chi tiết giao diện:**
   * Tên sản phẩm dài: Hiển thị rút gọn với dấu `...` và tích hợp Tooltip (`setToolTipText`) hiển thị đầy đủ thông tin khi rê chuột vào ô trên bảng.
   * Tìm kiếm tiếng Việt: Tự động chuẩn hóa loại bỏ dấu tiếng Việt (nhập "mi" có thể tìm ra "Mì Hảo Hảo", "Bánh mì").

---

## 🖥️ CÁC PHÂN HỆ CHỨC NĂNG CHÍNH

* **Màn hình Đăng nhập (Login):** Xác thực tài khoản người dùng, phân vai trò Admin hoặc Cashier, hỗ trợ đăng nhập nhanh cho quá trình kiểm thử.
* **Tab 1 - Quản lý Kho & Sản phẩm:** Xem danh sách sản phẩm, thêm, sửa, xóa, tìm kiếm theo tên hoặc mã, lọc theo danh mục, cảnh báo hàng tồn thấp hoặc cận hạn sử dụng, tạo phiếu đề xuất nhập hàng và xuất báo cáo kho hàng ra tệp CSV.
* **Tab 2 - Quầy thu ngân (POS):** Tìm kiếm sản phẩm, hỗ trợ thiết bị quét mã vạch Barcode (thông qua phím Enter), thêm nhanh vào giỏ hàng, điều chỉnh số lượng, lưu tạm đơn hàng (treo đơn), tính chiết khấu khách hàng thân thiết, tính tiền thừa trả khách và in phiếu hóa đơn bán lẻ.
* **Tab 3 - Lịch sử hóa đơn:** Hiển thị danh sách hóa đơn theo mô hình Master - Detail (bảng trên hiển thị danh sách hóa đơn, bảng dưới hiển thị chi tiết các mặt hàng đã mua kèm đơn giá tại thời điểm giao dịch), hỗ trợ xem lại phiếu hóa đơn và xuất báo cáo ra CSV.
* **Tab 4 - Thống kê & Cảnh báo:** Bảng điều khiển tài chính hỗ trợ lọc số liệu theo thời gian (Hôm nay, 7 ngày qua, Tháng này, Toàn bộ), hiển thị 4 chỉ số tổng hợp, biểu đồ trực quan doanh thu theo danh mục và theo ngày (sử dụng thư viện đồ họa Java 2D), bảng thống kê cơ cấu ngành hàng và cảnh báo hàng tồn thấp/cận hạn.

---

## 🚀 HƯỚNG DẪN CHẠY VÀ ĐÓNG GÓI CHƯƠNG TRÌNH

* **Cách 1 (Chạy tệp thực thi .jar):** Nhấp đúp chuột vào tệp **`MiniMartSystem.jar`** hoặc thực thi lệnh trong terminal:
  ```bash
  java -jar MiniMartSystem.jar
  ```
* **Cách 2 (Chạy nhanh bằng kịch bản batch script):** Nhấp đúp chuột vào tệp **`run.bat`**.
* **Cách 3 (Chạy trong môi trường phát triển - IDE):** Mở thư mục `MiniMartSystem` trong VS Code / Eclipse / IntelliJ IDEA, mở tệp `MainApp.java` và chọn **Run** (hoặc nhấn **F5**).
* **Đóng gói lại tệp .jar:** Nhấp đúp chuột vào tệp **`build_jar.bat`** để tự động biên dịch toàn bộ mã nguồn và xuất ra tệp thực thi `MiniMartSystem.jar`.

---

## 🏛️ GIẢI TRÌNH CÁC QUYẾT ĐỊNH THIẾT KẾ KIẾN TRÚC OOP (DESIGN RATIONALE)

### 1. Áp dụng Đa hình (Polymorphism) cho phương thức `calculateProfit()`
* **Vấn đề đặt ra:** Thay vì áp dụng công thức thô `Lợi nhuận = Giá bán - Giá nhập` trên lớp cơ sở `Product`, tại sao hệ thống lại định nghĩa `calculateProfit()` là phương thức trừu tượng (`abstract`) và yêu cầu từng lớp con ghi đè (`override`)?
* **Cơ sở thiết kế & Nghiệp vụ thực tế:**
  Trong thực tế vận hành siêu thị mini, mỗi ngành hàng chịu chi phí rủi ro và đặc thù kế toán hoàn toàn khác nhau. Việc tính lợi nhuận thô sẽ làm sai lệch báo cáo tài chính và hiệu quả kinh doanh:
  1. **Thực phẩm (`FoodProduct`):** Phải gánh chịu chi phí hao hụt tự nhiên và chi phí điện năng bảo quản mát (trừ 5% doanh thu). Khi thực phẩm cận hạn sử dụng ($\le$ 7 ngày), hệ thống tự động áp dụng chính sách chiết khấu 20% để kích cầu xả hàng, giảm thiểu tổn thất phải tiêu hủy toàn bộ.
  2. **Đồ uống (`BeverageProduct`):** Chịu chi phí lưu kho cồng kềnh và chi phí vận chuyển két, thùng nặng (trừ 3% doanh thu). Đối với các sản phẩm đồ uống có cồn (bia, rượu), hệ thống trích nộp 10% thuế tiêu thụ đặc biệt theo quy định.
  3. **Đồ điện gia dụng (`ElectronicProduct`):** Đơn vị bán lẻ chịu trách nhiệm bảo hành kỹ thuật, do đó hệ thống tự động trích lập 10% doanh thu vào Quỹ dự phòng bảo hành và dịch vụ sau bán hàng.
* **Kết luận OOP:** Phương thức đa hình `calculateProfit()` giúp mã nguồn tuân thủ nguyên lý **Open/Closed Principle (OCP)**: Khi mở rộng thêm ngành hàng mới (ví dụ: `StationeryProduct`), hệ thống chỉ cần bổ sung lớp mới mà không cần chỉnh sửa logic tính toán lợi nhuận tại các Service hay Controller hiện có.

---

### 2. Mô hình hóa Khách hàng: Kế thừa (Inheritance) & Định hướng mở rộng với Strategy Pattern
* **Vấn đề đặt ra:** Mối quan hệ giữa `Customer`, `RegularCustomer` và `VIPCustomer` được hiện thực hóa như thế nào khi phát sinh nghiệp vụ nâng hạng thành viên trong runtime?
* **Phân tích thiết kế:**
  * **Trong phạm vi đồ án học phần:** Sử dụng **Kế thừa (Inheritance)** nhằm mô hình hóa trực quan mối quan hệ *"is-a"* giữa khách hàng thông thường và các chính sách ưu đãi khác nhau (`RegularCustomer` hưởng 0% chiết khấu, `VIPCustomer` hưởng 5% chiết khấu tự động trên hóa đơn). Khi tích lũy doanh số đạt ngưỡng 5.000.000 đ, hệ thống chuyển giao trạng thái bằng cách khởi tạo phiên bản `VIPCustomer` kế thừa toàn bộ lịch sử chi tiêu và cập nhật vào `CustomerRepository`.
  * **Định hướng kiến trúc nâng cao (Enterprise Architecture):** Trong môi trường phát triển quy mô lớn, việc ép kiểu hoặc khởi tạo lại đối tượng có thể dẫn đến việc thay đổi tham chiếu đối tượng. Nhóm đã tìm hiểu và định hướng giải pháp áp dụng nguyên lý **Composition over Inheritance**:
    * Áp dụng **Strategy Pattern**: Lớp `Customer` đóng vai trò ngữ cảnh (Context) chứa một thuộc tính chiến lược chiết khấu `MembershipDiscountStrategy`.
    * Khi khách hàng đủ điều kiện nâng hạng, hệ thống chỉ cần thay đổi chiến lược: `customer.setDiscountStrategy(new VIPDiscountStrategy())`. Đối tượng khách hàng giữ nguyên tham chiếu duy nhất mà hành vi tính toán chiết khấu vẫn biến đổi linh hoạt và dễ dàng mở rộng thêm các hạng thành viên mới.

---

### 3. Chiến lược lưu vết đơn giá (Snapshot Pricing Pattern) trong Hóa đơn
* **Vấn đề đặt ra:** Khi lưu trữ chi tiết dòng hàng trong hóa đơn (`invoices.csv`), hệ thống lưu trữ mã sản phẩm hay chốt đơn giá bán tại thời điểm lập hóa đơn?
* **Cơ sở thiết kế:**
  * Hệ thống áp dụng **Snapshot Pricing Pattern**: Mỗi dòng sản phẩm trong hóa đơn được lưu trữ độc lập theo cấu trúc: `MãSP#TênSP#SốLượng#ĐơnGiáLúcBán`.
  * **Lý do bảo đảm toàn vẹn dữ liệu:** Giá niêm yết của sản phẩm trên kệ biến động theo thời gian (do điều chỉnh giá nhà cung cấp hoặc chính sách khuyến mãi). Nếu hóa đơn chỉ lưu mã sản phẩm rồi truy vấn động giá từ kho hàng hiện tại, tổng giá trị của các hóa đơn cũ trong quá khứ sẽ bị thay đổi so với số tiền khách đã trả. Việc lưu Snapshot Price bảo đảm tính bất biến (**Immutability**) và tính toàn vẹn của chứng từ tài chính kế toán.
