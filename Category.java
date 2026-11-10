public enum Category {
    FOOD("Thực phẩm"),
    BEVERAGE("Đồ uống"),
    ELECTRONIC("Đồ điện gia dụng"),
    HOUSEHOLD("Hàng tiêu dùng");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Category fromString(String text) {
        for (Category c : Category.values()) {
            if (c.name().equalsIgnoreCase(text) || c.displayName.equalsIgnoreCase(text)) {
                return c;
            }
        }
        return FOOD;
    }
}

