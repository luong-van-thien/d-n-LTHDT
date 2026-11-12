/**
 * Lớp sản phẩm Đồ điện tử / Gia dụng (ElectronicProduct) kế thừa từ Product.
 */
public class ElectronicProduct extends Product {
    private static final long serialVersionUID = 1L;

    private int warrantyMonths;    // Số tháng bảo hành (ví dụ: 12, 24)
    private int powerWatt;         // Công suất tiêu thụ (Watt, ví dụ: 1000W)

    public ElectronicProduct() {
        super();
        this.category = Category.ELECTRONIC;
    }

    public ElectronicProduct(String id, String name, double importPrice, double sellingPrice, int quantity,
                             int warrantyMonths, int powerWatt) {
        super(id, name, importPrice, sellingPrice, quantity, Category.ELECTRONIC);
        this.warrantyMonths = warrantyMonths;
        this.powerWatt = powerWatt;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        if (warrantyMonths < 0) throw new IllegalArgumentException("Tháng bảo hành không được âm!");
        this.warrantyMonths = warrantyMonths;
    }

    public int getPowerWatt() {
        return powerWatt;
    }

    public void setPowerWatt(int powerWatt) {
        if (powerWatt < 0) throw new IllegalArgumentException("Công suất không được âm!");
        this.powerWatt = powerWatt;
    }

    @Override
    public boolean isExpiringSoon() {
        // Thiết bị điện tử không có hạn sử dụng thông thường
        return false;
    }

    /**
     * Tính lợi nhuận đa hình (Polymorphism):
     * Đối với đồ điện gia dụng, siêu thị chịu trách nhiệm bảo hành, đổi trả hư hỏng sau bán hàng:
     * - Trích lập Quỹ dự phòng bảo hành & dịch vụ hậu mãi: 10% giá bán (nếu BH >= 12 tháng) hoặc 5% (nếu BH < 12 tháng).
     */
    @Override
    public double calculateProfit() {
        double warrantyRate = (warrantyMonths >= 12) ? 0.10 : 0.05; // Trích 10% nếu BH >= 12 tháng
        double warrantyReserve = sellingPrice * warrantyRate; // Quỹ bảo hành
        return (sellingPrice - importPrice) - warrantyReserve;
    }

    @Override
    public String getSpecificInfo() {
        return String.format("Bảo hành: %d tháng | Công suất: %d W", warrantyMonths, powerWatt);
    }
}

