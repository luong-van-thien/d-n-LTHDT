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

    public InvoiceRepository(CustomerRepository customerRepo) {
        loadFromFile(customerRepo);
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

                inv.setCustomerCash(Double.parseDouble(parts[6].trim()));
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
            writer.write("#InvoiceID,CreatedAt,CustomerID,TotalAmount,DiscountAmount,FinalAmount,CustomerCash,ReturnCash\n");
            for (Invoice inv : invoiceList) {
                String custId = inv.getCustomer() != null ? inv.getCustomer().getCustomerId() : "GUEST";
                writer.write(String.format("%s,%s,%s,%.0f,%.0f,%.0f,%.0f,%.0f\n",
                        inv.getInvoiceId(),
                        inv.getCreatedAt().format(TIME_FMT),
                        custId,
                        inv.getTotalAmount(),
                        inv.getDiscountAmount(),
                        inv.getFinalAmount(),
                        inv.getCustomerCash(),
                        inv.getReturnCash()));
            }
        } catch (IOException e) {
            System.err.println("Lỗi ghi file hóa đơn: " + e.getMessage());
        }
    }
}

