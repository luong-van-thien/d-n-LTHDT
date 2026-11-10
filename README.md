# 🛒 HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART SYSTEM)
> **Đồ án môn học:** Lập trình hướng đối tượng (Java)  
> **Giảng viên hướng dẫn:** ThS. Nguyễn Thị Tuyết Hải - Học viện Công nghệ Bưu chính Viễn thông (PTIT HCM)  
> **Nhóm sinh viên thực hiện:** Nhóm 4 thành viên  

---

## 👥 BẢNG PHÂN CÔNG NHIỆM VỤ & TỶ LỆ ĐÓNG GÓP (NHÓM 4 THÀNH VIÊN)

| STT | Họ và Tên | Mã Sinh Viên (MSSV) | Vai trò | Nhiệm vụ chính phụ trách | File code đảm nhiệm chính | Tỷ lệ đóng góp | Mức độ hoàn thành |
| :---: | :--- | :---: | :---: | :--- | :--- | :---: | :---: |
| **1** | **Lương Văn Thiện** | `N24DCCN076` | **Trưởng nhóm** | Thiết kế kiến trúc tổng thể, xây dựng các lớp đối tượng cốt lõi (OOP Entity), Kế thừa & Đa hình, điều phối dự án. | `Product.java`, `FoodProduct.java`, `BeverageProduct.java`, `ElectronicProduct.java`, `Customer.java`, `RegularCustomer.java`, `VIPCustomer.java` | **25%** | **100% (Tốt)** |
| **2** | *[Họ tên Thành viên 2]* | `[MSSV]` | Thành viên | Xây dựng giao diện Quản lý Kho, bảng JTable dữ liệu, thanh tìm kiếm & sắp xếp bằng Lambda Expressions & Comparator. | `ProductPanel.java`, `ProductController.java`, `ProductService.java` | **25%** | **100% (Tốt)** |
| **3** | *[Họ tên Thành viên 3]* | `[MSSV]` | Thành viên | Xây dựng phân hệ Quầy Thu Ngân (POS), giỏ hàng, tính chiết khấu đa hình, trừ tồn kho và logic xuất in hóa đơn bán lẻ. | `PosPanel.java`, `PosController.java`, `OrderService.java`, `Invoice.java`, `InvoiceItem.java` | **25%** | **100% (Tốt)** |
| **4** | *[Họ tên Thành viên 4]* | `[MSSV]` | Thành viên | Xây dựng tầng Đọc/Ghi dữ liệu File CSV, hệ thống Custom Exceptions, màn hình Dashboard Thống kê & Giám sát cảnh báo. | `ProductRepository.java`, `CustomerRepository.java`, `InvoiceRepository.java`, `StatisticPanel.java`, `StatisticService.java`, `*Exception.java` | **25%** | **100% (Tốt)** |

> **Tổng tỷ lệ đóng góp toàn nhóm:** 100% (Mỗi thành viên đóng góp đều 25%, hoàn thành đúng tiến độ và yêu cầu đề ra).

---

## 🚀 1. Hướng Dẫn Chạy Dự Án Trên Visual Studio Code (VS Code)

### Cách 1: Chạy trực tiếp trên VS Code bằng 1 click
1. Mở **Visual Studio Code**, chọn **File $\rightarrow$ Open Folder...** và chọn thư mục `MiniMartSystem`.
2. Mở file [MainApp.java](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/MainApp.java).
3. Bấm nút **Run** (biểu tượng ▶️ góc trên bên phải) hoặc ấn phím **F5** (hoặc `Ctrl + F5`).

### Cách 2: Double-click file `.bat` (Siêu tiện lợi)
* Trong thư mục `MiniMartSystem`, chỉ cần **Double-click vào file `run.bat`**. Chương trình sẽ tự chạy giao diện đồ họa Swing ngay!
* Nếu bạn chỉnh sửa code và muốn biên dịch lại: Double-click vào file `compile.bat`.

---

## 🎯 2. Tổng Quan 4 Tab Chức Năng Trên Giao Diện

1. **📦 Tab 1: Quản Lý Kho & Sản Phẩm ([`ProductPanel.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/ProductPanel.java))**
   - Xem toàn bộ danh sách hàng hóa trong kho.
   - Tìm kiếm theo từ khóa, lọc theo danh mục, sắp xếp bằng **Lambda Expression & Comparator** (Tên A-Z, Tên Z-A, Giá bán, Tồn kho, Lợi nhuận).
   - Nút **"⚠️ Cảnh báo tồn/hạn"** phát hiện ngay hàng tồn ít $\le 5$ hoặc thực phẩm sắp hết hạn date $< 7$ ngày.
   - Form Thêm/Sửa/Xóa với bắt lỗi chặt chẽ bằng các Custom Exception.

2. **💳 Tab 2: Quầy Thu Ngân - POS Bán Hàng ([`PosPanel.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/PosPanel.java))**
   - Chọn sản phẩm $\rightarrow$ nhập số lượng mua $\rightarrow$ thêm vào giỏ hàng (tự động chặn nếu kho không đủ hàng).
   - Chọn loại khách hàng: Khách vãng lai, Khách thường (giảm 2%), Khách VIP (giảm 5% - 10%). Tự động áp dụng **tính đa hình chiết khấu**.
   - Tự tính tiền thừa thối lại khi nhập tiền khách đưa.
   - Nút **"💳 THANH TOÁN & IN HÓA ĐƠN"**: tự động trừ tồn kho, tích điểm khách hàng và **xuất file hóa đơn bán lẻ text (`HD-xxx.txt`)** ngay trong thư mục.

3. **📜 Tab 3: Lịch Sử Hóa Đơn ([`InvoiceHistoryPanel.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/InvoiceHistoryPanel.java))**
   - Bảng tra cứu toàn bộ đơn hàng đã giao dịch.
   - Nút **"📄 Mở File Hóa Đơn In (.txt)"** để xem biên lai mua hàng.

4. **📊 Tab 4: Thống Kê & Cảnh Báo ([`StatisticPanel.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/StatisticPanel.java))**
   - 4 Card Dashboard: Tổng doanh thu, Lợi nhuận ước tính, Số hóa đơn, Tổng tồn kho.
   - Bảng cảnh báo khẩn cấp các mặt hàng sắp hết hoặc hết hạn.

---

## 💯 3. Đối Chiếu Kiến Thức Điểm 10 Với Slide Cô Tuyết Hải

| Khái niệm OOP (Slide) | Cài đặt cụ thể trong mã nguồn |
| :--- | :--- |
| **Đóng gói (Encapsulation)** | Thuộc tính `private`/`protected`, đầy đủ `getter/setter`, validate chặn giá âm trong [`Product.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/Product.java). |
| **Kế thừa (Inheritance)** | Lớp cha [`Product.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/Product.java) $\rightarrow$ [`FoodProduct.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/FoodProduct.java), [`BeverageProduct.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/BeverageProduct.java), [`ElectronicProduct.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/ElectronicProduct.java).<br>Lớp cha [`Customer.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/Customer.java) $\rightarrow$ [`RegularCustomer.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/RegularCustomer.java), [`VIPCustomer.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/VIPCustomer.java). |
| **Đa hình (Polymorphism)** | Phương thức trừu tượng `calculateProfit()` tính lợi nhuận riêng biệt cho từng mặt hàng.<br>Phương thức `calculateDiscount()` tính chiết khấu riêng cho từng nhóm khách hàng.<br>Ghi đè `toString()`, `equals()`, `hashCode()` từ `Object`. |
| **Interfaces & Lambda** | [`Discountable.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/Discountable.java), [`Exportable.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/Exportable.java). Cú pháp Lambda `(p1, p2) -> ...` sắp xếp dữ liệu và bắt sự kiện nút. |
| **Xử lý Ngoại lệ (Exception)** | 4 lớp ngoại lệ: [`DuplicateIdException.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/DuplicateIdException.java), [`OutOfStockException.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/OutOfStockException.java), [`InvalidDataException.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/InvalidDataException.java), [`NotFoundException.java`](file:///d:/HK1%202025-2026/lập%20trình%20hướng%20đối%20tượng/MiniMartSystem/NotFoundException.java). |
| **Collections Framework** | `HashMap` tra cứu nhanh theo mã ID, `ArrayList` quản lý danh sách bảng, `HashSet` đảm bảo tính duy nhất. |
| **Swing GUI & MVC Pattern** | Giao diện trực quan `JFrame`, `JTabbedPane`, `JTable`, tách bạch rõ ràng giữa Model, View và Controller. |
| **Lưu trữ tệp (File I/O)** | Dữ liệu bền vững lưu trong `products.csv`, `customers.csv`, `invoices.csv`. |
