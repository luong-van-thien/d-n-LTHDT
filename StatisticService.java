import java.util.List;

/**
 * Lớp Dịch vụ Thống kê & Báo cáo doanh thu (StatisticService).
 */
public class StatisticService {
    private final ProductRepository productRepo;
    private final InvoiceRepository invoiceRepo;

    public StatisticService(ProductRepository productRepo, InvoiceRepository invoiceRepo) {
        this.productRepo = productRepo;
        this.invoiceRepo = invoiceRepo;
    }

    public double calculateTotalRevenue() {
        return invoiceRepo.getAll().stream()
                .mapToDouble(Invoice::getFinalAmount)
                .sum();
    }

    public double calculateTotalEstimatedProfit() {
        return invoiceRepo.getAll().stream()
                .mapToDouble(Invoice::calculateTotalProfit)
                .sum();
    }

    public int getTotalInvoicesCount() {
        return invoiceRepo.getAll().size();
    }

    public int getTotalInventoryQuantity() {
        return productRepo.getAll().stream()
                .mapToInt(Product::getQuantity)
                .sum();
    }

    public long getLowStockCount(int threshold) {
        return productRepo.getAll().stream()
                .filter(p -> p.isLowStock(threshold))
                .count();
    }

    public long getExpiringCount() {
        return productRepo.getAll().stream()
                .filter(Product::isExpiringSoon)
                .count();
    }

    public double calculateTotalInventoryValue() {
        return productRepo.getAll().stream()
                .mapToDouble(p -> p.getImportPrice() * p.getQuantity())
                .sum();
    }

    public long getCountByCategory(Category cat) {
        return productRepo.getAll().stream()
                .filter(p -> p.getCategory() == cat)
                .count();
    }

    public int getQuantityByCategory(Category cat) {
        return productRepo.getAll().stream()
                .filter(p -> p.getCategory() == cat)
                .mapToInt(Product::getQuantity)
                .sum();
    }

    public double getInventoryValueByCategory(Category cat) {
        return productRepo.getAll().stream()
                .filter(p -> p.getCategory() == cat)
                .mapToDouble(p -> p.getImportPrice() * p.getQuantity())
                .sum();
    }
}

