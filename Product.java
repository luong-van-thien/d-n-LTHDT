import java.io.Serializable;
import java.util.Objects;

/**
 * Lớp trừu tượng đại diện cho một sản phẩm trong hệ thống siêu thị mini.
 * Áp dụng tính Đóng gói (Encapsulation), Kế thừa (Inheritance), và Đa hình (Polymorphism).
 */
public abstract class Product implements Comparable<Product>, Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected double importPrice;
    protected double sellingPrice;
    protected int quantity;
    protected Category category;

    public Product() {
    }

    public Product(String id, String name, double importPrice, double sellingPrice, int quantity, Category category) {
        this.id = id;
        this.name = name;
        this.importPrice = importPrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(double importPrice) {
        if (importPrice < 0) {
            throw new IllegalArgumentException("Giá nhập không được âm!");
        }
        this.importPrice = importPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        if (sellingPrice < 0) {
            throw new IllegalArgumentException("Giá bán không được âm!");
        }
        this.sellingPrice = sellingPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm!");
        }
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Phương thức trừu tượng: Tính lợi nhuận ước tính trên một đơn vị sản phẩm.
     * Mỗi lớp con sẽ có cách tính riêng (đa hình).
     */
    public abstract double calculateProfit();

    /**
     * Phương thức trừu tượng: Trả về chuỗi thông tin đặc trưng riêng biệt của loại sản phẩm.
     */
    public abstract String getSpecificInfo();

    /**
     * Kiểm tra xem sản phẩm có đang thuộc trạng thái cần cảnh báo không (sắp hết hạn, lỗi bảo hành...)
     */
    public abstract boolean isExpiringSoon();

    /**
     * Kiểm tra tồn kho có ở mức báo động không (dưới ngưỡng tối thiểu)
     */
    public boolean isLowStock(int minThreshold) {
        return this.quantity <= minThreshold;
    }

    @Override
    public int compareTo(Product other) {
        if (other == null) return 1;
        int nameCompare = this.name.compareToIgnoreCase(other.name);
        if (nameCompare != 0) {
            return nameCompare;
        }
        return this.id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Loại: %s | Giá: %,.0f đ | Tồn: %d",
                id, name, category.getDisplayName(), sellingPrice, quantity);
    }
}

