import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Lớp sản phẩm Thực phẩm (FoodProduct) kế thừa từ Product.
 * Có hạn sử dụng và ngày sản xuất.
 */
public class FoodProduct extends Product {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private LocalDate manufactureDate;
    private LocalDate expirationDate;

    public FoodProduct() {
        super();
        this.category = Category.FOOD;
    }

    public FoodProduct(String id, String name, double importPrice, double sellingPrice, int quantity,
                       LocalDate manufactureDate, LocalDate expirationDate) {
        super(id, name, importPrice, sellingPrice, quantity, Category.FOOD);
        this.manufactureDate = manufactureDate;
        this.expirationDate = expirationDate;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Kiểm tra thực phẩm có sắp hết hạn sử dụng không (còn dưới 7 ngày hoặc đã hết hạn).
     */
    @Override
    public boolean isExpiringSoon() {
        if (expirationDate == null) return false;
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
        return daysRemaining <= 7;
    }

    public boolean isExpired() {
        if (expirationDate == null) return false;
        return expirationDate.isBefore(LocalDate.now());
    }

    /**
     * Tính lợi nhuận:
     * Nếu sắp hết hạn (<= 7 ngày), siêu thị giảm giá 20% để xả hàng, lợi nhuận tính theo giá giảm.
     */
    @Override
    public double calculateProfit() {
        double currentSellingPrice = sellingPrice;
        if (isExpiringSoon()) {
            currentSellingPrice = sellingPrice * 0.8; // Giảm giá xả hàng
        }
        return currentSellingPrice - importPrice;
    }

    @Override
    public String getSpecificInfo() {
        String nsx = manufactureDate != null ? manufactureDate.format(DATE_FORMATTER) : "N/A";
        String hsd = expirationDate != null ? expirationDate.format(DATE_FORMATTER) : "N/A";
        String status = "";
        if (isExpired()) {
            status = " [ĐÃ HẾT HẠN!]";
        } else if (isExpiringSoon()) {
            status = " [CẢNH BÁO: Sắp hết hạn!]";
        }
        return String.format("NSX: %s | HSD: %s%s", nsx, hsd, status);
    }
}

