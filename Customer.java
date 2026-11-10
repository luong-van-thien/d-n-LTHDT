import java.io.Serializable;
import java.util.Objects;

/**
 * Lớp trừu tượng đại diện cho Khách hàng.
 * Cài đặt Interface Discountable để tính toán chiết khấu đa hình.
 */
public abstract class Customer implements Discountable, Serializable {
    private static final long serialVersionUID = 1L;

    protected String customerId;
    protected String name;
    protected String phone;
    protected double totalSpent; // Tổng chi tiêu tích lũy

    public Customer() {
    }

    public Customer(String customerId, String name, String phone, double totalSpent) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.totalSpent = totalSpent;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public void addSpent(double amount) {
        if (amount > 0) {
            this.totalSpent += amount;
        }
    }

    public int getLoyaltyPoints() {
        return (int) (totalSpent / 100000); // Khách thường tích lũy 1 điểm mỗi 100.000 đ chi tiêu
    }

    /**
     * Phương thức trừu tượng: Tỷ lệ chiết khấu (%) theo từng loại khách hàng.
     */
    public abstract double getDiscountRate();

    /**
     * Tên loại khách hàng (Thường, VIP...)
     */
    public abstract String getCustomerType();

    @Override
    public double calculateDiscount(double amount) {
        return amount * getDiscountRate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s, Tích lũy: %,.0f đ)", customerId, name, getCustomerType(), totalSpent);
    }
}

