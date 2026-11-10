# HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG
### KHOA CÔNG NGHỆ THÔNG TIN - CƠ SỞ TP. HỒ CHÍ MINH
---

# BÁO CÁO ĐỒ ÁN GIỮA KỲ
## ĐỀ TÀI: XÂY DỰNG HỆ THỐNG QUẢN LÝ BÁN HÀNG VÀ KHO MINI (MINIMART SYSTEM)

* **Môn học:** Lập trình hướng đối tượng (Object-Oriented Programming)
* **Mã học phần:** INT1340
* **Giảng viên hướng dẫn:** ThS. Nguyễn Thị Tuyết Hải
* **Học kỳ:** Học kỳ 1 - Năm học 2025 - 2026

---

## 👥 BẢNG PHÂN CÔNG NHIỆM VỤ & TỶ LỆ ĐÓNG GÓP THÀNH VIÊN

| STT | Họ và Tên | Mã Sinh Viên (MSSV) | Vai trò | Nhiệm vụ đảm nhiệm chính | Các module / File mã nguồn phụ trách | Tỷ lệ đóng góp | Đánh giá |
| :---: | :--- | :---: | :---: | :--- | :--- | :---: | :---: |
| **1** | **Lương Văn Thiện** | `N24DCCN076` | **Trưởng nhóm** | Phân tích bài toán, thiết kế kiến trúc hệ thống, xây dựng các lớp thực thể OOP (Kế thừa, Đa hình, Trừu tượng), điều phối và tích hợp dự án. | `Product.java`, `FoodProduct.java`, `BeverageProduct.java`, `ElectronicProduct.java`, `Customer.java`, `RegularCustomer.java`, `VIPCustomer.java`, `Category.java`, `Discountable.java` | **25%** | **Hoàn thành tốt** |
| **2** | **Trần Nhật Nguyên** | `N22DCCN057` | Thành viên | Thiết kế và lập trình phân hệ Quản lý Kho & Danh mục sản phẩm, bảng dữ liệu `JTable`, cơ chế lọc và sắp xếp nâng cao với Lambda Expression & Comparator. | `ProductPanel.java`, `ProductController.java`, `ProductService.java` | **25%** | **Hoàn thành tốt** |
| **3** | **Nguyễn Hải Phong** | `N24DCCN059` | Thành viên | Thiết kế và lập trình phân hệ Quầy thu ngân (POS), xử lý nghiệp vụ giỏ hàng, tính chiết khấu khách hàng đa hình, trừ tồn kho và xuất hóa đơn bán lẻ. | `PosPanel.java`, `PosController.java`, `OrderService.java`, `Invoice.java`, `InvoiceItem.java`, `Exportable.java` | **25%** | **Hoàn thành tốt** |
| **4** | **Ngô Đức Thành** | `N24DCCN074` | Thành viên | Xây dựng tầng lưu trữ tệp tin (File I/O CSV), hệ thống xử lý ngoại lệ tùy biến (Custom Exceptions), phân hệ Báo cáo thống kê Dashboard và cảnh báo khẩn. | `ProductRepository.java`, `CustomerRepository.java`, `InvoiceRepository.java`, `StatisticPanel.java`, `StatisticService.java`, `*Exception.java` | **25%** | **Hoàn thành tốt** |

> **Tổng kết:** 100% (Mỗi thành viên hoàn thành đầy đủ 25% khối lượng công việc theo đúng tiến độ đề ra).

---

## 📖 1. Giới Thiệu Đề Tài & Mục Tiêu

### 1.1. Đặt vấn đề
Trong hoạt động kinh doanh bán lẻ tại các chuỗi cửa hàng tiện lợi và siêu thị mini, khối lượng mặt hàng đa dạng và lượng giao dịch hàng ngày rất lớn. Việc ghi chép thủ công dễ phát sinh sai sót trong khâu tính tiền, thất thoát tồn kho và không kiểm soát được hạn sử dụng của hàng hóa. Đồ án **MiniMart System** được xây dựng nhằm cung cấp giải pháp tự động hóa quy trình quản lý kho, hỗ trợ thu ngân lập hóa đơn chính xác, đồng thời cung cấp báo cáo thống kê trực quan cho người quản trị.

### 1.2. Mục tiêu kỹ thuật
* Áp dụng toàn diện 4 tính chất nền tảng của Lập trình hướng đối tượng: **Đóng gói (Encapsulation)**, **Kế thừa (Inheritance)**, **Đa hình (Polymorphism)** và **Trừu tượng (Abstraction)**.
* Ứng dụng mô hình kiến trúc **MVC (Model - View - Controller)** giúp phân tách rõ ràng giữa dữ liệu nghiệp vụ, giao diện người dùng và tầng điều khiển sự kiện.
* Tích hợp các kỹ thuật hiện đại của Java: **Lambda Expressions**, **Stream API**, **Java Collections Framework**, **Custom Exceptions** và lưu trữ dữ liệu bền vững qua **File I/O (CSV)**.
* Xây dựng giao diện đồ họa **Java Swing** chuyên nghiệp, trực quan và tối ưu trải nghiệm người dùng.

---

## 🏛️ 2. Kiến Trúc Hệ Thống & Ánh Xạ Kiến Thức OOP

Hệ thống được thiết kế theo đúng chuẩn kiến trúc hướng đối tượng được giảng dạy trong học phần:

### 2.1. Tính đóng gói (Encapsulation)
* Tất cả các thuộc tính dữ liệu trong các lớp thực thể (`Product`, `Customer`, `Invoice`...) đều được khai báo với phạm vi truy cập `private` hoặc `protected`.
* Truy cập và hiệu chỉnh dữ liệu thông qua hệ thống phương thức `getter/setter` chuẩn mực.
* Thực hiện ràng buộc dữ liệu chặt chẽ ngay tại phương thức gán (ví dụ: không cho phép giá nhập, giá bán hoặc số lượng tồn kho nhận giá trị âm).

### 2.2. Tính kế thừa (Inheritance)
* **Phân cấp Hàng hóa:** Lớp cha trừu tượng `Product` định nghĩa các thuộc tính và hành vi chung của sản phẩm. Các lớp con chuyên biệt kế thừa và mở rộng:
  * `FoodProduct`: Bổ sung Ngày sản xuất (`manufactureDate`), Hạn sử dụng (`expirationDate`).
  * `BeverageProduct`: Bổ sung Dung tích (`volumeMl`), Nồng độ cồn (`alcoholic`).
  * `ElectronicProduct`: Bổ sung Thời gian bảo hành (`warrantyMonths`), Công suất (`powerWatt`).
* **Phân cấp Khách hàng:** Lớp cha `Customer` được mở rộng bởi `RegularCustomer` (khách thông thường) và `VIPCustomer` (khách hàng thân thiết có điểm thưởng tích lũy).

### 2.3. Tính đa hình & Lớp trừu tượng (Polymorphism & Abstraction)
* Phương thức trừu tượng `calculateProfit()` trong `Product` được ghi đè (`@Override`) với logic nghiệp vụ riêng biệt tại từng lớp con:
  * Thực phẩm sắp hết hạn ($< 7$ ngày) sẽ tự động áp dụng chính sách giảm giá xả hàng, lợi nhuận tính theo giá sau chiết khấu.
  * Đồ uống có cồn được tự động trừ 10% thuế tiêu thụ đặc biệt vào lợi nhuận.
  * Hàng điện tử được tự động trích lập 5% giá trị vào quỹ dự phòng bảo hành.
* Phương thức `calculateDiscount()` được đa hình hóa thông qua `Customer`: Khách thường nhận chiết khấu 2% khi đạt ngưỡng chi tiêu, khách VIP nhận chiết khấu 5% đến 10% dựa theo điểm thưởng tích lũy.
* Ghi đè các phương thức tiêu chuẩn của lớp tối cao `Object`: `toString()`, `equals()`, `hashCode()` và hiện thực giao thức sắp xếp `Comparable<Product>`.

### 2.4. Giao diện (Interfaces) & Biểu thức Lambda (Lambda Expressions)
* Định nghĩa các Interface nghiệp vụ:
  * `Discountable`: Định nghĩa hành vi tính toán giảm giá.
  * `Exportable`: Định nghĩa khả năng xuất bản dữ liệu hóa đơn ra tệp văn bản.
* Ứng dụng **Lambda Expressions** `(p1, p2) -> ...` kết hợp với `Comparator` để hiện thực đa dạng các tiêu chí sắp xếp động (theo tên A-Z/Z-A, theo giá bán tăng/giảm, theo số lượng tồn kho, theo tỷ suất lợi nhuận).
* Ứng dụng Lambda trong cơ chế ủy quyền lắng nghe sự kiện Swing (`ActionListener`).

### 2.5. Xử lý ngoại lệ tùy biến (Custom Exception Handling)
Hệ thống xây dựng gói ngoại lệ chuyên biệt kế thừa từ `java.lang.Exception`:
* `DuplicateIdException`: Ngăn chặn hành vi nhập trùng mã định danh sản phẩm hoặc khách hàng.
* `OutOfStockException`: Chặn giao dịch khi số lượng khách yêu cầu vượt quá tồn kho thực tế.
* `InvalidDataException`: Kiểm soát toàn diện dữ liệu nhập vào (để trống, sai định dạng số/ngày, giá trị logic không hợp lệ).
* `NotFoundException`: Thông báo khi thao tác trên các bản ghi không tồn tại trong hệ thống.
* Sử dụng triệt để cấu trúc `try - catch - finally` kết hợp `JOptionPane` để xử lý lỗi an toàn, không để ứng dụng bị dừng đột ngột.

### 2.6. Cấu trúc dữ liệu (Java Collections Framework) & Lưu trữ tệp (File I/O)
* `HashMap<String, Product>` và `HashMap<String, Customer>`: Hỗ trợ tìm kiếm, truy xuất thông tin với độ phức tạp $O(1)$.
* `ArrayList`: Quản lý danh sách phục vụ hiển thị trên `JTable` và chi tiết hóa đơn.
* `HashSet<String>`: Đảm bảo tính duy nhất của mã sản phẩm trong kho.
* Cơ chế lưu trữ bền vững (Persistence): Dữ liệu được đồng bộ liên tục vào các tệp tin `products.csv`, `customers.csv` và `invoices.csv` theo chuẩn mã hóa UTF-8.

---

## 🖥️ 3. Mô Tả Các Phân Hệ Chức Năng

Ứng dụng cung cấp 4 phân hệ làm việc độc lập trên thanh điều hướng `JTabbedPane`:

### 3.1. Phân hệ Quản lý Kho & Danh mục Hàng hóa (`ProductPanel`)
* **Hiển thị trực quan:** Bảng dữ liệu hiển thị chi tiết mã hàng, tên, phân loại, giá nhập, giá niêm yết, tồn kho, lợi nhuận dự kiến và thông số kỹ thuật đặc thù.
* **Bộ lọc & Tìm kiếm:** Tìm kiếm tức thời theo từ khóa, lọc theo nhóm ngành hàng, sắp xếp đa tiêu chí.
* **Cảnh báo hàng hóa:** Nút chức năng lọc nhanh các sản phẩm đang ở mức cảnh báo (tồn kho $\le 5$ đơn vị hoặc thực phẩm có hạn sử dụng dưới 7 ngày).
* **Cập nhật dữ liệu:** Form tiếp nhận thông tin với khả năng chuyển đổi trường nhập liệu linh hoạt (`CardLayout`) theo từng loại hàng hóa.

### 3.2. Phân hệ Quầy Thu ngân & Bán lẻ (`PosPanel`)
* **Lập đơn hàng:** Tìm kiếm sản phẩm, chọn số lượng và chuyển vào giỏ hàng với cơ chế tự động kiểm tra số dư tồn kho.
* **Chính sách khách hàng:** Hỗ trợ giao dịch khách vãng lai hoặc liên kết tài khoản thành viên thân thiết; tự động tính tỷ lệ chiết khấu đa hình.
* **Tính toán thanh toán:** Tự động tính tổng tiền, chiết khấu, tiền phải trả và tiền thừa trả lại khách hàng.
* **In hóa đơn:** Trừ kho tức thời, tích điểm thành viên và tự động xuất hóa đơn bán lẻ chi tiết dưới dạng tệp văn bản `.txt`.

### 3.3. Phân hệ Quản lý Lịch sử Hóa đơn (`InvoiceHistoryPanel`)
* Lưu trữ và hiển thị toàn bộ lịch sử các giao dịch đã thực hiện trong ca bán hàng.
* Cung cấp công cụ tra cứu hóa đơn theo mã số hoặc tên khách hàng.
* Tích hợp tính năng mở trực tiếp bản in hóa đơn gốc bằng trình đọc văn bản mặc định của hệ điều hành.

### 3.4. Phân hệ Báo cáo Thống kê & Giám sát (`StatisticPanel`)
* **Chỉ số KPI thời gian thực:** Thống kê tổng doanh thu bán hàng, ước tính lợi nhuận gộp thực tế, tổng số lượt giao dịch và tổng lượng hàng tồn kho.
* **Giám sát rủi ro:** Bảng cảnh báo tập trung các mặt hàng đã hết hoặc sắp hết hạn sử dụng để hỗ trợ người quản lý lên kế hoạch nhập hàng hoặc kích hoạt chính sách thanh lý kịp thời.

---

## 🛠️ 4. Hướng Dẫn Cài Đặt & Vận Hành Hệ Thống

### 4.1. Yêu cầu môi trường
* **Hệ điều hành:** Windows 10 / Windows 11 / Linux / macOS.
* **Môi trường Java:** Java Development Kit (JDK) phiên bản 8 trở lên (Khuyến nghị JDK 17 hoặc JDK 21 LTS).
* **Công cụ phát triển:** Visual Studio Code, IntelliJ IDEA hoặc Eclipse.

### 4.2. Khởi chạy dự án

#### Phương thức 1: Vận hành trực tiếp trên Visual Studio Code
1. Mở thư mục dự án `MiniMartSystem` bằng VS Code (`File -> Open Folder...`).
2. Mở tệp tin `MainApp.java`.
3. Nhấn phím **F5** (hoặc nút **Run** ở góc trên bên phải) để khởi chạy chương trình.

#### Phương thức 2: Vận hành nhanh bằng tập lệnh thực thi
* Trong thư mục dự án, thực hiện nhấp đúp chuột vào tệp tin **`run.bat`**. Tập lệnh sẽ tự động kiểm tra, biên dịch mã nguồn và khởi động giao diện ứng dụng.
* Để biên dịch lại toàn bộ dự án độc lập, nhấp đúp vào tệp tin **`compile.bat`**.

---

## 📌 5. Kết Luận
Đồ án **MiniMart System** đã hoàn thành đầy đủ các yêu cầu đề ra, thể hiện rõ tư duy thiết kế hướng đối tượng chặt chẽ, kiến trúc mã nguồn trong sáng và khả năng ứng dụng thực tế cao. Nhóm sinh viên xin trân trọng cảm ơn cô **Nguyễn Thị Tuyết Hải** đã tận tình hướng dẫn và truyền đạt kiến thức trong suốt học phần.
