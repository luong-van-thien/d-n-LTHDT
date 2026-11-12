import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Hóa đơn bán lẻ (Invoice).
 * Cài đặt Interface Exportable để xuất hóa đơn ra file text.
 */
public class Invoice implements Exportable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String invoiceId;
    private LocalDateTime createdAt;
    private Customer customer;
    private List<InvoiceItem> items = new ArrayList<>();
    private double totalAmount;      // Tổng tiền gốc
    private double discountAmount;   // Tiền giảm giá
    private double finalAmount;      // Tiền thanh toán thực tế
    private double customerCash;     // Tiền khách đưa
    private double returnCash;       // Tiền thừa trả lại

    public Invoice() {
        this.createdAt = LocalDateTime.now();
    }

    public Invoice(String invoiceId, Customer customer) {
        this.invoiceId = invoiceId;
        this.createdAt = LocalDateTime.now();
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public double getCustomerCash() {
        return customerCash;
    }

    public void setCustomerCash(double customerCash) {
        this.customerCash = customerCash;
    }

    public double getReturnCash() {
        return returnCash;
    }

    public void setReturnCash(double returnCash) {
        this.returnCash = returnCash;
    }

    public void addItem(InvoiceItem item) {
        this.items.add(item);
    }

    /**
     * Tính toán tổng tiền, chiết khấu và tiền trả khách.
     */
    public void calculateSummary() {
        totalAmount = 0.0;
        for (InvoiceItem item : items) {
            totalAmount += item.getSubTotal();
        }

        if (customer != null) {
            discountAmount = customer.calculateDiscount(totalAmount);
        } else {
            discountAmount = 0.0;
        }

        finalAmount = Math.max(0, totalAmount - discountAmount);
        returnCash = Math.max(0, customerCash - finalAmount);
    }

    public double calculateTotalProfit() {
        double profit = 0.0;
        for (InvoiceItem item : items) {
            profit += item.getEstimatedProfit();
        }
        return profit - discountAmount;
    }

    @Override
    public boolean exportToFile(String destinationPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(destinationPath), StandardCharsets.UTF_8))) {
            writer.write("============================================================\n");
            writer.write("                       SIÊU THỊ MINI                        \n");
            writer.write("           Học viện Công nghệ Bưu chính Viễn thông          \n");
            writer.write("                     HÓA ĐƠN BÁN LẺ                         \n");
            writer.write("============================================================\n");
            writer.write(String.format("Mã hóa đơn: %s\n", invoiceId));
            writer.write(String.format("Thời gian : %s\n", createdAt.format(TIME_FORMATTER)));
            writer.write(String.format("Khách hàng: %s\n", customer != null ? customer.getName() : "Khách vãng lai"));
            if (customer != null) {
                writer.write(String.format("Loại KH   : %s | SĐT: %s\n", customer.getCustomerType(), customer.getPhone()));
            }
            writer.write("------------------------------------------------------------\n");
            writer.write(String.format("%-22s %-6s %-14s %-14s\n", "Tên SP", "SL", "Đơn giá", "Thành tiền"));
            writer.write("------------------------------------------------------------\n");
            for (InvoiceItem item : items) {
                String pName = item.getProduct().getName();
                if (pName.length() > 20) {
                    pName = pName.substring(0, 18) + "..";
                }
                writer.write(String.format("%-22s %-6d %,-14.0f %,-14.0f\n",
                        pName, item.getQuantity(), item.getUnitPrice(), item.getSubTotal()));
            }
            writer.write("------------------------------------------------------------\n");
            writer.write(String.format("Tổng tiền hàng  : %,.0f VNĐ\n", totalAmount));
            writer.write(String.format("Chiết khấu      : %,.0f VNĐ\n", discountAmount));
            writer.write(String.format("THANH TOÁN      : %,.0f VNĐ\n", finalAmount));
            writer.write(String.format("Tiền khách đưa  : %,.0f VNĐ\n", customerCash));
            writer.write(String.format("Tiền thối lại   : %,.0f VNĐ\n", returnCash));
            writer.write("============================================================\n");
            writer.write("           CẢM ƠN QUÝ KHÁCH & HẸN GẶP LẠI!                  \n");
            writer.write("============================================================\n");
            return true;
        }
    }
}

