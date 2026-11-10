import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Lớp Xử lý nghiệp vụ Đơn hàng & Thanh toán (OrderService).
 */
public class OrderService {
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final InvoiceRepository invoiceRepo;

    public OrderService(ProductRepository productRepo, CustomerRepository customerRepo, InvoiceRepository invoiceRepo) {
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.invoiceRepo = invoiceRepo;
    }

    /**
     * Tiến hành thanh toán đơn hàng (Checkout).
     */
    public synchronized Invoice checkout(Customer customer, List<InvoiceItem> cartItems, double cashGiven)
            throws InvalidDataException, OutOfStockException, IOException {

        if (cartItems == null || cartItems.isEmpty()) {
            throw new InvalidDataException("Giỏ hàng đang trống! Vui lòng chọn ít nhất 1 sản phẩm.");
        }

        // 1. Kiểm tra tồn kho trước khi thực hiện trừ hàng
        for (InvoiceItem item : cartItems) {
            Product currentProduct = productRepo.findById(item.getProduct().getId());
            if (currentProduct == null) {
                throw new InvalidDataException("Sản phẩm [" + item.getProduct().getName() + "] không còn tồn tại trong hệ thống!");
            }
            if (currentProduct.getQuantity() < item.getQuantity()) {
                throw new OutOfStockException(String.format("Sản phẩm '%s' không đủ hàng trong kho! (Còn: %d, Yêu cầu: %d)",
                        currentProduct.getName(), currentProduct.getQuantity(), item.getQuantity()));
            }
        }

        // 2. Tạo mã hóa đơn tự động theo thời gian
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String invoiceId = "HD-" + timestamp;

        Invoice invoice = new Invoice(invoiceId, customer);
        for (InvoiceItem item : cartItems) {
            invoice.addItem(item);
        }
        invoice.setCustomerCash(cashGiven);
        invoice.calculateSummary();

        if (cashGiven < invoice.getFinalAmount()) {
            throw new InvalidDataException(String.format("Tiền khách đưa (%,.0f đ) chưa đủ để thanh toán (Cần: %,.0f đ)!",
                    cashGiven, invoice.getFinalAmount()));
        }

        // 3. Cập nhật trừ số lượng hàng trong kho
        for (InvoiceItem item : cartItems) {
            Product p = productRepo.findById(item.getProduct().getId());
            int remaining = p.getQuantity() - item.getQuantity();
            productRepo.updateQuantity(p.getId(), remaining);
        }

        // 4. Cập nhật tích lũy chi tiêu và điểm cho khách hàng
        if (customer != null) {
            customerRepo.updateSpentAndPoints(customer.getCustomerId(), invoice.getFinalAmount());
        }

        // 5. Lưu hóa đơn vào hệ thống
        invoiceRepo.save(invoice);

        // 6. Xuất hóa đơn ra file text ngay trong thư mục
        String receiptPath = invoiceId + ".txt";
        invoice.exportToFile(receiptPath);

        return invoice;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepo.getAll();
    }
}

