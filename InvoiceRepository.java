import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Lớp Quản lý dữ liệu Hóa đơn (InvoiceRepository).
 */
public class InvoiceRepository {
    private static final String FILE_PATH = "invoices.csv";
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<Invoice> invoiceList = new ArrayList<>();
    private final Map<String, Invoice> invoiceMap = new HashMap<>();
    private ProductRepository productRepo;

    public InvoiceRepository(CustomerRepository customerRepo) {
        this(customerRepo, null);
    }

    public InvoiceRepository(CustomerRepository customerRepo, ProductRepository productRepo) {
        this.productRepo = productRepo;
        loadFromFile(customerRepo);
    }

    public void setProductRepo(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public synchronized List<Invoice> getAll() {
        return new ArrayList<>(invoiceList);
    }

    public synchronized Invoice findById(String id) {
        if (id == null) return null;
        return invoiceMap.get(id.trim().toUpperCase());
    }

    public synchronized void save(Invoice invoice) {
        String id = invoice.getInvoiceId().trim().toUpperCase();
        invoice.setInvoiceId(id);

        invoiceList.add(0, invoice); // Thêm mới lên đầu danh sách để dễ xem
        invoiceMap.put(id, invoice);
        saveToFile();
    }

    private void loadFromFile(CustomerRepository customerRepo) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        invoiceList.clear();
        invoiceMap.clear();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 8) continue;

                String id = parts[0].trim();
                LocalDateTime time = LocalDateTime.parse(parts[1].trim(), TIME_FMT);
                String custId = parts[2].trim();
                Customer customer = customerRepo.findById(custId);

                Invoice inv = new Invoice(id, customer);
                inv.setCreatedAt(time);

                inv.setTotalAmount(Double.parseDouble(parts[3].trim()));
                inv.setDiscountAmount(Double.parseDouble(parts[4].trim()));
                inv.setFinalAmount(Double.parseDouble(parts[5].trim()));
                inv.setCustomerCash(Double.parseDouble(parts[6].trim()));
                inv.setReturnCash(Double.parseDouble(parts[7].trim()));

                if (parts.length >= 9 && !parts[8].trim().isEmpty()) {
                    String[] itemTokens = parts[8].trim().split(";");
                    for (String it : itemTokens) {
                        String[] p = it.split("#");
                        if (p.length >= 3) {
                            String pId = p[0].trim();
                            int qty = Integer.parseInt(p[1].trim());
                            double price = Double.parseDouble(p[2].trim());
                            Product prod = (productRepo != null) ? productRepo.findById(pId) : null;
                            if (prod == null) {
                                prod = new FoodProduct(pId, "Sản phẩm " + pId, price * 0.8, price, qty, java.time.LocalDate.now(), java.time.LocalDate.now().plusMonths(6));
                            }
                            inv.addItem(new InvoiceItem(prod, qty, price));
                        }
                    }
                }

                invoiceList.add(inv);
                invoiceMap.put(id, inv);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đọc file hóa đơn: " + e.getMessage());
        }
    }

    public synchronized void saveToFile() {
        File file = new File(FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("#InvoiceID,CreatedAt,CustomerID,TotalAmount,DiscountAmount,FinalAmount,CustomerCash,ReturnCash,Items\n");
            for (Invoice inv : invoiceList) {
                String custId = inv.getCustomer() != null ? inv.getCustomer().getCustomerId() : "GUEST";
                StringBuilder sbItems = new StringBuilder();
                List<InvoiceItem> items = inv.getItems();
                for (int i = 0; i < items.size(); i++) {
                    InvoiceItem item = items.get(i);
                    sbItems.append(item.getProduct().getId()).append("#")
                           .append(item.getQuantity()).append("#")
                           .append(String.format(Locale.US, "%.0f", item.getUnitPrice()));
                    if (i < items.size() - 1) sbItems.append(";");
                }
                writer.write(String.format(Locale.US, "%s,%s,%s,%.0f,%.0f,%.0f,%.0f,%.0f,%s\n",
                        inv.getInvoiceId(),
                        inv.getCreatedAt().format(TIME_FMT),
                        custId,
                        inv.getTotalAmount(),
                        inv.getDiscountAmount(),
                        inv.getFinalAmount(),
                        inv.getCustomerCash(),
                        inv.getReturnCash(),
                        sbItems.toString()));
            }
        } catch (IOException e) {
            System.err.println("Lỗi ghi file hóa đơn: " + e.getMessage());
        }
    }
}

