import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Lớp sản phẩm Đồ uống (BeverageProduct) kế thừa từ Product.
 */
public class BeverageProduct extends Product {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int volumeMl;          // Thể tích tính bằng ml (ví dụ: 330, 500, 1500)
    private boolean alcoholic;     // Có cồn hay không (Bia, rượu...)
    private LocalDate expirationDate;

    public BeverageProduct() {
        super();
        this.category = Category.BEVERAGE;
    }

    public BeverageProduct(String id, String name, double importPrice, double sellingPrice, int quantity,
                           int volumeMl, boolean alcoholic, LocalDate expirationDate) {
        super(id, name, importPrice, sellingPrice, quantity, Category.BEVERAGE);
        this.volumeMl = volumeMl;
        this.alcoholic = alcoholic;
        this.expirationDate = expirationDate;
    }

    public int getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(int volumeMl) {
        if (volumeMl < 0) throw new IllegalArgumentException("Dung tích không được âm!");
        this.volumeMl = volumeMl;
    }

    public boolean isAlcoholic() {
        return alcoholic;
    }

    public void setAlcoholic(boolean alcoholic) {
        this.alcoholic = alcoholic;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean isExpiringSoon() {
        if (expirationDate == null) return false;
        long days = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
        return days <= 10;
    }

    /**
     * Tính lợi nhuận đa hình (Polymorphism):
     * Đối với đồ uống trong siêu thị:
     * 1. Nếu là đồ uống có cồn (bia, rượu): Phải trích nộp 10% Thuế tiêu thụ đặc biệt trên giá bán.
     * 2. Phí lưu kho và vận chuyển hàng lỏng cồng kềnh (thùng, két, lon/chai): trích 3% giá bán.
     */
    @Override
    public double calculateProfit() {
        double baseProfit = sellingPrice - importPrice;
        double storageCost = sellingPrice * 0.03; // 3% phí vận chuyển & lưu kho cồng kềnh
        double specialTax = alcoholic ? (sellingPrice * 0.10) : 0.0; // 10% thuế TTĐB nếu có cồn
        return baseProfit - storageCost - specialTax;
    }

    @Override
    public String getSpecificInfo() {
        String hsd = expirationDate != null ? expirationDate.format(DATE_FORMATTER) : "Không ghi";
        String conStr = alcoholic ? "Có cồn" : "Không cồn";
        return String.format("%d ml | %s | HSD: %s", volumeMl, conStr, hsd);
    }
}

