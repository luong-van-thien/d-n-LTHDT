/**
 * Lớp đại diện cho Tài khoản Quản lý / Quản trị viên (Admin).
 * Kế thừa từ lớp trừu tượng User và thực thi đa hình các quyền hạn cao nhất.
 */
public class AdminUser extends User {

    public AdminUser(String username, String password, String fullName) {
        super(username, password, fullName, Role.ADMIN);
    }

    @Override
    public boolean canAccessTab(int tabIndex) {
        // Quản trị viên có toàn quyền truy cập tất cả 4 Tab
        return true;
    }

    @Override
    public boolean canModifyProduct() {
        // Quản trị viên được phép thêm, sửa, xóa, nhập kho
        return true;
    }

    @Override
    public boolean canViewStatistics() {
        // Quản trị viên được phép xem báo cáo tài chính, doanh thu, lợi nhuận
        return true;
    }

    @Override
    public boolean canExportData() {
        // Quản trị viên có quyền xuất báo cáo ra file Excel / CSV
        return true;
    }

    @Override
    public String getRoleDescription() {
        return "Toàn quyền quản trị kho, nhập xuất hàng, thanh toán và báo cáo doanh thu";
    }
}

