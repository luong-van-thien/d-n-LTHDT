import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Invoice> getFilteredInvoices(int timeMode) {
        LocalDate today = LocalDate.now();
        return invoiceRepo.getAll().stream()
                .filter(inv -> {
                    LocalDate invDate = inv.getCreatedAt().toLocalDate();
                    switch (timeMode) {
                        case 1: // Hôm nay
                            return invDate.isEqual(today);
                        case 2: // 7 ngày qua
                            return inv.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7));
                        case 3: // Tháng này
                            return invDate.getMonth() == today.getMonth() && invDate.getYear() == today.getYear();
                        default: // 0: Tất cả
                            return true;
                    }
                })
                .collect(Collectors.toList());
    }

    public double calculateTotalRevenue() {
        return calculateTotalRevenue(0);
    }

    public double calculateTotalRevenue(int timeMode) {
        return getFilteredInvoices(timeMode).stream()
                .mapToDouble(Invoice::getFinalAmount)
                .sum();
    }

    public double calculateTotalEstimatedProfit() {
        return calculateTotalEstimatedProfit(0);
    }

    public double calculateTotalEstimatedProfit(int timeMode) {
        return getFilteredInvoices(timeMode).stream()
                .mapToDouble(Invoice::calculateTotalProfit)
                .sum();
    }

    public int getTotalInvoicesCount() {
        return getTotalInvoicesCount(0);
    }

    public int getTotalInvoicesCount(int timeMode) {
        return getFilteredInvoices(timeMode).size();
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

    public double calculateTotalRetailValue() {
        return productRepo.getAll().stream()
                .mapToDouble(p -> p.getSellingPrice() * p.getQuantity())
                .sum();
    }

    public long getSafeStockCount() {
        return productRepo.getAll().stream()
                .filter(p -> p.getQuantity() > 5)
                .count();
    }

    public long getOutOfStockCount() {
        return productRepo.getAll().stream()
                .filter(p -> p.getQuantity() == 0)
                .count();
    }

    public double getAverageOrderValue() {
        return getAverageOrderValue(0);
    }

    public double getAverageOrderValue(int timeMode) {
        List<Invoice> list = getFilteredInvoices(timeMode);
        if (list.isEmpty()) return 0.0;
        return calculateTotalRevenue(timeMode) / list.size();
    }

    public double getTotalDiscountGiven() {
        return getTotalDiscountGiven(0);
    }

    public double getTotalDiscountGiven(int timeMode) {
        return getFilteredInvoices(timeMode).stream()
                .mapToDouble(Invoice::getDiscountAmount)
                .sum();
    }
}

