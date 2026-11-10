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
     * Tính lợi nhuận:
     * Nếu là đồ uống có cồn, phải trích nộp 10% thuế tiêu thụ đặc biệt trên giá bán.
     */
    @Override
    public double calculateProfit() {
        double profit = sellingPrice - importPrice;
        if (alcoholic) {
            double tax = sellingPrice * 0.10; // Thuế tiêu thụ đặc biệt
            profit -= tax;
        }
        return profit;
    }

    @Override
    public String getSpecificInfo() {
        String hsd = expirationDate != null ? expirationDate.format(DATE_FORMATTER) : "Không ghi";
        String conStr = alcoholic ? "Có cồn" : "Không cồn";
        return String.format("%d ml | %s | HSD: %s", volumeMl, conStr, hsd);
    }
}

