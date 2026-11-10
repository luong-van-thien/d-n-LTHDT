import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Lớp Quản lý dữ liệu Khách hàng (CustomerRepository).
 */
public class CustomerRepository {
    private static final String FILE_PATH = "customers.csv";

    private final Map<String, Customer> customerMap = new HashMap<>();
    private final List<Customer> customerList = new ArrayList<>();

    public CustomerRepository() {
        loadFromFile();
    }

    public synchronized List<Customer> getAll() {
        return new ArrayList<>(customerList);
    }

    public synchronized Customer findById(String id) {
        if (id == null) return null;
        return customerMap.get(id.trim().toUpperCase());
    }

    public synchronized Customer findByPhone(String phone) {
        if (phone == null) return null;
        for (Customer c : customerList) {
            if (c.getPhone() != null && c.getPhone().equals(phone.trim())) {
                return c;
            }
        }
        return null;
    }

    public synchronized void save(Customer customer) {
        String id = customer.getCustomerId().trim().toUpperCase();
        customer.setCustomerId(id);

        if (customerMap.containsKey(id)) {
            int index = customerList.indexOf(customerMap.get(id));
            if (index >= 0) {
                customerList.set(index, customer);
            }
        } else {
            customerList.add(customer);
        }
        customerMap.put(id, customer);
        saveToFile();
    }

    public synchronized void updateSpentAndPoints(String customerId, double amount) {
        Customer c = findById(customerId);
        if (c != null) {
            c.addSpent(amount);
            if (c instanceof VIPCustomer) {
                // Mỗi 10.000 VNĐ cộng 1 điểm loyalty
                int earnedPoints = (int) (amount / 10000);
                ((VIPCustomer) c).addPoints(earnedPoints);
            }
            saveToFile();
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            initSampleData();
            return;
        }

        customerMap.clear();
        customerList.clear();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;

                String type = parts[0].trim();
                String id = parts[1].trim().toUpperCase();
                String name = parts[2].trim();
                String phone = parts[3].trim();
                double spent = Double.parseDouble(parts[4].trim());

                Customer c;
                if ("VIP".equalsIgnoreCase(type)) {
                    int points = parts.length > 5 ? Integer.parseInt(parts[5].trim()) : 0;
                    c = new VIPCustomer(id, name, phone, spent, points);
                } else {
                    c = new RegularCustomer(id, name, phone, spent);
                }

                customerMap.put(id, c);
                customerList.add(c);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đọc file khách hàng: " + e.getMessage());
            if (customerList.isEmpty()) {
                initSampleData();
            }
        }
    }

    public synchronized void saveToFile() {
        File file = new File(FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("#Type,ID,Name,Phone,TotalSpent,LoyaltyPoints\n");
            for (Customer c : customerList) {
                if (c instanceof VIPCustomer) {
                    VIPCustomer vip = (VIPCustomer) c;
                    writer.write(String.format("VIP,%s,%s,%s,%.0f,%d\n",
                            vip.getCustomerId(), vip.getName(), vip.getPhone(), vip.getTotalSpent(), vip.getLoyaltyPoints()));
                } else {
                    writer.write(String.format("REGULAR,%s,%s,%s,%.0f,0\n",
                            c.getCustomerId(), c.getName(), c.getPhone(), c.getTotalSpent()));
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi ghi file khách hàng: " + e.getMessage());
        }
    }

    private void initSampleData() {
        customerMap.clear();
        customerList.clear();

        save(new RegularCustomer("KH001", "Nguyễn Văn An", "0901234567", 500000));
        save(new RegularCustomer("KH002", "Trần Thị Bích", "0912345678", 2500000));
        save(new VIPCustomer("VIP001", "Lê Hoàng Nam", "0987654321", 8500000, 350));
        save(new VIPCustomer("VIP002", "Phạm Quỳnh Chi", "0933445566", 15200000, 620));

        saveToFile();
    }
}

