/**
 * Enum đại diện cho các vai trò trong hệ thống siêu thị.
 */
public enum Role {
    ADMIN("Quản trị viên"),
    CASHIER("Nhân viên thu ngân");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

