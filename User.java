/**
 * Lớp trừu tượng đại diện cho Người dùng trong hệ thống (User).
 * Áp dụng tính Trừu tượng (Abstraction), Đóng gói (Encapsulation)
 * và Đa hình (Polymorphism) để phân quyền chức năng (Role-Based Access Control).
 */
public abstract class User {
    private final String username;
    private String password;
    private String fullName;
    private final Role role;

    public User(String username, String password, String fullName, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // --- Các phương thức trừu tượng thể hiện tính ĐA HÌNH (Polymorphism) ---

    /**
     * Kiểm tra người dùng có quyền truy cập tab tương ứng hay không.
     * @param tabIndex 0: Kho hàng, 1: POS, 2: Lịch sử, 3: Thống kê & Báo cáo
     * @return true nếu có quyền, ngược lại false
     */
    public abstract boolean canAccessTab(int tabIndex);

    /**
     * Quyền thêm, sửa, xóa, nhập hàng trong kho.
     */
    public abstract boolean canModifyProduct();

    /**
     * Quyền xem doanh thu, lợi nhuận và thống kê tài chính.
     */
    public abstract boolean canViewStatistics();

    /**
     * Quyền xuất dữ liệu ra tệp Excel / CSV.
     */
    public abstract boolean canExportData();

    /**
     * Mô tả chi tiết quyền hạn của tài khoản.
     */
    public abstract String getRoleDescription();

    // --- Getters & Setters ---

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %s)", fullName, username, role.getDisplayName());
    }
}
