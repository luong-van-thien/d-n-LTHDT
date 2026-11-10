/**
 * Khách hàng VIP (VIPCustomer) có điểm thưởng loyaltyPoints và chiết khấu cao hơn.
 */
public class VIPCustomer extends Customer {
    private static final long serialVersionUID = 1L;

    private int loyaltyPoints; // Điểm tích lũy thành viên VIP

    public VIPCustomer() {
        super();
    }

    public VIPCustomer(String customerId, String name, String phone, double totalSpent, int loyaltyPoints) {
        super(customerId, name, phone, totalSpent);
        this.loyaltyPoints = loyaltyPoints;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public void addPoints(int points) {
        if (points > 0) {
            this.loyaltyPoints += points;
        }
    }

    @Override
    public double getDiscountRate() {
        // Khách VIP thông thường được giảm 5%, nếu điểm tích lũy >= 500 điểm được giảm 10%
        if (loyaltyPoints >= 500) {
            return 0.10;
        }
        return 0.05;
    }

    @Override
    public String getCustomerType() {
        if (loyaltyPoints >= 500) {
            return "VIP Kim Cương (Giảm 10%)";
        }
        return "VIP Vàng (Giảm 5%)";
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s, Điểm: %d, Tích lũy: %,.0f đ)",
                customerId, name, getCustomerType(), loyaltyPoints, totalSpent);
    }
}

