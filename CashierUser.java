/**
 * Lớp đại diện cho Tài khoản Nhân viên Thu ngân (Cashier).
 * Kế thừa từ lớp trừu tượng User và thực thi đa hình các quyền hạn giới hạn:
 * - Bán hàng và thanh toán tại quầy POS.
 * - Xem lịch sử hóa đơn.
 * - Tra cứu tồn kho (Chỉ đọc - không được sửa kho).
 * - KHÔNG có quyền xem báo cáo doanh thu & lợi nhuận.
 */
public class CashierUser extends User {

    public CashierUser(String username, String password, String fullName) {
        super(username, password, fullName, Role.CASHIER);
    }

    @Override
    public boolean canAccessTab(int tabIndex) {
        // Tab 3 là Thống kê & Doanh thu -> Thu ngân KHÔNG được phép truy cập
        return tabIndex != 3;
    }

    @Override
    public boolean canModifyProduct() {
        // Thu ngân KHÔNG được phép thêm, sửa, xóa sản phẩm trong kho
        return false;
    }

    @Override
    public boolean canViewStatistics() {
        // Thu ngân KHÔNG được phép xem doanh thu và lợi nhuận cửa hàng
        return false;
    }

    @Override
    public boolean canExportData() {
        // Thu ngân KHÔNG được phép xuất dữ liệu báo cáo nội bộ
        return false;
    }

    @Override
    public String getRoleDescription() {
        return "Bán hàng POS, in hóa đơn và tra cứu số lượng tồn kho (Chế độ chỉ đọc)";
    }
}
