/**
 * Khách hàng thân thiết thông thường (RegularCustomer).
 */
public class RegularCustomer extends Customer {
    private static final long serialVersionUID = 1L;

    public RegularCustomer() {
        super();
    }

    public RegularCustomer(String customerId, String name, String phone, double totalSpent) {
        super(customerId, name, phone, totalSpent);
    }

    @Override
    public double getDiscountRate() {
        // Nếu tổng chi tiêu đạt trên 2 triệu đồng, được giảm 2%
        if (totalSpent >= 2_000_000) {
            return 0.02;
        }
        return 0.0;
    }

    @Override
    public String getCustomerType() {
        return "Khách Thường";
    }
}

