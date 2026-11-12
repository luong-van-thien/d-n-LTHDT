/**
 * Enum đại diện cho các vai trò trong hệ thống siêu thị.
 */
public enum Role {
    ADMIN("Quản lý (Admin)"),
    CASHIER("Nhân viên thu ngân (Cashier)");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
