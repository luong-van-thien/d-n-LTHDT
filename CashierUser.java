/**
 * Lớp đại diện cho Tài khoản Nhân viên Thu ngân (Cashier).
 * Kế thừa từ lớp trừu tượng User và thực thi đa hình các quyền hạn giới hạn:
 * - Tab 1 (Kho hàng): BỊ KHÓA HOÀN TOÀN để ngăn chặn việc sửa giá và can thiệp tồn kho.
 * - Tab 2 (POS): Toàn quyền bán hàng và thanh toán tại quầy.
 * - Tab 3 (Lịch sử hóa đơn): Xem các hóa đơn đã giao dịch.
 * - Tab 4 (Thống kê & Doanh thu): BỊ KHÓA HOÀN TOÀN để bảo mật tài chính.
 */
public class CashierUser extends User {

    public CashierUser(String username, String password, String fullName) {
        super(username, password, fullName, Role.CASHIER);
    }

    @Override
    public boolean canAccessTab(int tabIndex) {
        // Tab 0: Quản lý kho -> KHÓA (false)
        // Tab 1: Quầy POS -> MỞ (true)
        // Tab 2: Lịch sử hóa đơn -> MỞ (true)
        // Tab 3: Thống kê & Báo cáo -> KHÓA (false)
        return tabIndex == 1 || tabIndex == 2;
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
        return "Bán hàng POS và Tra cứu Lịch sử Hóa đơn (Khóa Tab Kho hàng & Khóa Tab Thống kê)";
    }
}
