import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Lớp Quản lý dữ liệu Sản phẩm (Repository).
 * Ứng dụng Java Collections: Map (HashMap), List (ArrayList), Set (HashSet).
 * Đọc và ghi dữ liệu ra tệp tin CSV để dữ liệu luôn được lưu trữ bền vững.
 */
public class ProductRepository {
    private static final String FILE_PATH = "products.csv";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final Map<String, Product> productMap = new HashMap<>();
    private final List<Product> productList = new ArrayList<>();
    private final Set<String> idSet = new HashSet<>();

    public ProductRepository() {
        loadFromFile();
    }

    public synchronized List<Product> getAll() {
        return new ArrayList<>(productList);
    }

    public synchronized Product findById(String id) {
        if (id == null) return null;
        return productMap.get(id.trim().toUpperCase());
    }

    public synchronized boolean existsById(String id) {
        if (id == null) return false;
        return idSet.contains(id.trim().toUpperCase());
    }

    public synchronized void save(Product product) {
        String id = product.getId().trim().toUpperCase();
        product.setId(id);

        if (productMap.containsKey(id)) {
            // Update
            int index = productList.indexOf(productMap.get(id));
            if (index >= 0) {
                productList.set(index, product);
            }
        } else {
            // Insert
            productList.add(product);
            idSet.add(id);
        }
        productMap.put(id, product);
        saveToFile();
    }

    public synchronized boolean delete(String id) {
        if (id == null) return false;
        id = id.trim().toUpperCase();
        Product removed = productMap.remove(id);
        if (removed != null) {
            productList.remove(removed);
            idSet.remove(id);
            saveToFile();
            return true;
        }
        return false;
    }

    public synchronized void updateQuantity(String productId, int newQuantity) {
        Product p = findById(productId);
        if (p != null) {
            p.setQuantity(newQuantity);
            saveToFile();
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            initSampleData();
            return;
        }

        productMap.clear();
        productList.clear();
        idSet.clear();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                String id = parts[0].trim().toUpperCase();
                String name = parts[1].trim();
                double importPrice = Double.parseDouble(parts[2].trim());
                double sellingPrice = Double.parseDouble(parts[3].trim());
                int qty = Integer.parseInt(parts[4].trim());
                Category cat = Category.fromString(parts[5].trim());

                Product p = null;
                switch (cat) {
                    case FOOD:
                        LocalDate nsx = (parts.length > 6 && !parts[6].isEmpty()) ? LocalDate.parse(parts[6].trim(), DATE_FMT) : LocalDate.now();
                        LocalDate hsd = (parts.length > 7 && !parts[7].isEmpty()) ? LocalDate.parse(parts[7].trim(), DATE_FMT) : LocalDate.now().plusMonths(3);
                        p = new FoodProduct(id, name, importPrice, sellingPrice, qty, nsx, hsd);
                        break;
                    case BEVERAGE:
                        int vol = (parts.length > 6 && !parts[6].isEmpty()) ? Integer.parseInt(parts[6].trim()) : 330;
                        boolean alc = (parts.length > 7 && !parts[7].isEmpty()) && Boolean.parseBoolean(parts[7].trim());
                        LocalDate hsdB = (parts.length > 8 && !parts[8].isEmpty()) ? LocalDate.parse(parts[8].trim(), DATE_FMT) : LocalDate.now().plusMonths(6);
                        p = new BeverageProduct(id, name, importPrice, sellingPrice, qty, vol, alc, hsdB);
                        break;
                    case ELECTRONIC:
                        int war = (parts.length > 6 && !parts[6].isEmpty()) ? Integer.parseInt(parts[6].trim()) : 12;
                        int watt = (parts.length > 7 && !parts[7].isEmpty()) ? Integer.parseInt(parts[7].trim()) : 1000;
                        p = new ElectronicProduct(id, name, importPrice, sellingPrice, qty, war, watt);
                        break;
                    default:
                        p = new FoodProduct(id, name, importPrice, sellingPrice, qty, LocalDate.now(), LocalDate.now().plusMonths(12));
                        break;
                }

                if (p != null) {
                    productMap.put(id, p);
                    productList.add(p);
                    idSet.add(id);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đọc file sản phẩm: " + e.getMessage());
            if (productList.isEmpty()) {
                initSampleData();
            }
        }
    }

    public synchronized void saveToFile() {
        File file = new File(FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("#ID,Name,ImportPrice,SellingPrice,Quantity,Category,Extra1,Extra2,Extra3\n");
            for (Product p : productList) {
                StringBuilder sb = new StringBuilder();
                sb.append(p.getId()).append(",");
                sb.append(p.getName()).append(",");
                sb.append(p.getImportPrice()).append(",");
                sb.append(p.getSellingPrice()).append(",");
                sb.append(p.getQuantity()).append(",");
                sb.append(p.getCategory().name()).append(",");

                if (p instanceof FoodProduct) {
                    FoodProduct fp = (FoodProduct) p;
                    sb.append(fp.getManufactureDate() != null ? fp.getManufactureDate().format(DATE_FMT) : "").append(",");
                    sb.append(fp.getExpirationDate() != null ? fp.getExpirationDate().format(DATE_FMT) : "");
                } else if (p instanceof BeverageProduct) {
                    BeverageProduct bp = (BeverageProduct) p;
                    sb.append(bp.getVolumeMl()).append(",");
                    sb.append(bp.isAlcoholic()).append(",");
                    sb.append(bp.getExpirationDate() != null ? bp.getExpirationDate().format(DATE_FMT) : "");
                } else if (p instanceof ElectronicProduct) {
                    ElectronicProduct ep = (ElectronicProduct) p;
                    sb.append(ep.getWarrantyMonths()).append(",");
                    sb.append(ep.getPowerWatt());
                }
                sb.append("\n");
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            System.err.println("Lỗi ghi file sản phẩm: " + e.getMessage());
        }
    }

    private void initSampleData() {
        productMap.clear();
        productList.clear();
        idSet.clear();

        LocalDate now = LocalDate.now();
        save(new FoodProduct("SP001", "Mì Hảo Hảo Tôm Chua Cay", 3500, 5000, 80, now.minusMonths(1), now.plusMonths(5)));
        save(new FoodProduct("SP002", "Bánh Quy Oreo Socola 133g", 12000, 18000, 45, now.minusMonths(2), now.plusMonths(4)));
        save(new FoodProduct("SP003", "Sữa Tươi Tiệt Trùng Vinamilk 1L", 28000, 36000, 4, now.minusMonths(1), now.plusDays(4)));
        save(new FoodProduct("SP004", "Xúc Xích Tiệt Trùng Vissan", 15000, 22000, 35, now.minusMonths(1), now.plusMonths(2)));

        save(new BeverageProduct("SP005", "Nước Ngọt Coca Cola 330ml", 7000, 11000, 120, 330, false, now.plusMonths(9)));
        save(new BeverageProduct("SP006", "Bia Heineken Lon 330ml", 16000, 23000, 60, 330, true, now.plusMonths(8)));
        save(new BeverageProduct("SP007", "Trà Xanh Không Độ 500ml", 6500, 10000, 50, 500, false, now.plusMonths(6)));
        save(new BeverageProduct("SP008", "Nước Khoáng Lavie 500ml", 4000, 6000, 3, 500, false, now.plusMonths(12)));

        save(new ElectronicProduct("SP009", "Ấm Đun Nước Siêu Tốc Sunhouse 1.8L", 120000, 185000, 15, 12, 1500));
        save(new ElectronicProduct("SP010", "Nồi Cơm Điện Mini Sharp 1.2L", 350000, 520000, 8, 24, 700));
        save(new ElectronicProduct("SP011", "Máy Sấy Tóc Philips 1200W", 180000, 270000, 12, 12, 1200));

        saveToFile();
    }
}

