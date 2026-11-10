import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Xử lý nghiệp vụ Sản phẩm (ProductService).
 * Ứng dụng Lambda Expressions, Stream API và Comparator (Slide Chapter 6, 9).
 */
public class ProductService {
    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> getAllProducts() {
        return productRepo.getAll();
    }

    public Product getProductById(String id) {
        return productRepo.findById(id);
    }

    public void addProduct(Product product) throws DuplicateIdException, InvalidDataException {
        if (product == null) {
            throw new InvalidDataException("Dữ liệu sản phẩm không được rỗng!");
        }
        if (product.getId() == null || product.getId().trim().isEmpty()) {
            throw new InvalidDataException("Mã sản phẩm không được để trống!");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidDataException("Tên sản phẩm không được để trống!");
        }
        if (product.getImportPrice() < 0 || product.getSellingPrice() < 0) {
            throw new InvalidDataException("Giá sản phẩm không được âm!");
        }
        if (product.getQuantity() < 0) {
            throw new InvalidDataException("Số lượng tồn kho không được âm!");
        }
        if (productRepo.existsById(product.getId())) {
            throw new DuplicateIdException("Mã sản phẩm [" + product.getId() + "] đã tồn tại trong hệ thống!");
        }

        productRepo.save(product);
    }

    public void updateProduct(Product product) throws NotFoundException, InvalidDataException {
        if (product == null || product.getId() == null) {
            throw new InvalidDataException("Thông tin sản phẩm cập nhật không hợp lệ!");
        }
        if (!productRepo.existsById(product.getId())) {
            throw new NotFoundException("Không tìm thấy sản phẩm có mã: " + product.getId());
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidDataException("Tên sản phẩm không được để trống!");
        }
        if (product.getImportPrice() < 0 || product.getSellingPrice() < 0) {
            throw new InvalidDataException("Giá sản phẩm không được âm!");
        }
        if (product.getQuantity() < 0) {
            throw new InvalidDataException("Số lượng tồn kho không được âm!");
        }

        productRepo.save(product);
    }

    public void deleteProduct(String id) throws NotFoundException {
        boolean deleted = productRepo.delete(id);
        if (!deleted) {
            throw new NotFoundException("Không tìm thấy sản phẩm có mã: " + id);
        }
    }

    /**
     * Tìm kiếm và lọc sản phẩm kết hợp Lambda Expression.
     */
    public List<Product> searchAndFilter(String keyword, Category category) {
        String query = (keyword == null) ? "" : keyword.trim().toLowerCase();

        return productRepo.getAll().stream()
                .filter(p -> {
                    boolean matchKeyword = query.isEmpty() ||
                            p.getId().toLowerCase().contains(query) ||
                            p.getName().toLowerCase().contains(query);
                    boolean matchCategory = (category == null) || (p.getCategory() == category);
                    return matchKeyword && matchCategory;
                })
                .collect(Collectors.toList());
    }

    /**
     * Sắp xếp danh sách sản phẩm sử dụng Comparator kết hợp Lambda (Slide Chapter 6 & 9).
     */
    public List<Product> sortProducts(Comparator<Product> comparator) {
        List<Product> list = new ArrayList<>(productRepo.getAll());
        list.sort(comparator);
        return list;
    }

    /**
     * Lọc các sản phẩm tồn kho ít (cảnh báo nhập hàng)
     */
    public List<Product> getLowStockProducts(int threshold) {
        return productRepo.getAll().stream()
                .filter(p -> p.isLowStock(threshold))
                .collect(Collectors.toList());
    }

    /**
     * Lọc các sản phẩm sắp hết hạn / cần xả hàng
     */
    public List<Product> getExpiringProducts() {
        return productRepo.getAll().stream()
                .filter(Product::isExpiringSoon)
                .collect(Collectors.toList());
    }
}

