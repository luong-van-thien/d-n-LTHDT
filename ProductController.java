import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * Lớp Điều khiển Nghiệp vụ Sản phẩm (ProductController) theo mô hình MVC.
 * Bắt các sự kiện chuột, bàn phím và nút bấm trên ProductPanel.
 */
public class ProductController {
    private final ProductPanel view;
    private final ProductService productService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public ProductController(ProductPanel view, ProductService productService) {
        this.view = view;
        this.productService = productService;

        initListeners();
        loadAllProducts();
    }

    private void initListeners() {
        // 1. Sự kiện chọn dòng trên bảng -> Nạp dữ liệu vào form
        view.getTblProducts().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = view.getTblProducts().getSelectedRow();
                if (selectedRow >= 0) {
                    String id = (String) view.getTblProducts().getValueAt(selectedRow, 0);
                    displayProductDetails(id);
                }
            }
        });

        // 2. Nút Thêm mới (Add)
        view.getBtnAdd().addActionListener(e -> handleAddProduct());

        // 3. Nút Cập nhật (Update)
        view.getBtnUpdate().addActionListener(e -> handleUpdateProduct());

        // 4. Nút Xóa (Delete)
        view.getBtnDelete().addActionListener(e -> handleDeleteProduct());

        // 5. Nút Xóa trắng form (Clear)
        view.getBtnClear().addActionListener(e -> clearForm());

        // 6. Tìm kiếm & Lọc tức thời khi gõ ký tự (Live Search)
        view.getBtnSearch().addActionListener(e -> handleSearch());
        view.getTxtSearch().addActionListener(e -> handleSearch());
        view.getTxtSearch().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { handleSearch(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { handleSearch(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { handleSearch(); }
        });
        view.getCboFilterCategory().addActionListener(e -> handleSearch());

        // 7. Nút Làm mới bộ lọc
        view.getBtnResetFilter().addActionListener(e -> {
            view.getTxtSearch().setText("");
            view.getCboFilterCategory().setSelectedIndex(0);
            view.getCboSort().setSelectedIndex(0);
            loadAllProducts();
        });

        // 8. Nút Cảnh báo hàng tồn ít / sắp hết hạn
        view.getBtnAlertFilter().addActionListener(e -> {
            List<Product> lowStock = productService.getLowStockProducts(5);
            List<Product> expiring = productService.getExpiringProducts();
            lowStock.addAll(expiring.stream().filter(p -> !lowStock.contains(p)).toList());
            view.updateTable(lowStock);
            JOptionPane.showMessageDialog(view,
                    String.format("Đã phát hiện %d sản phẩm cần chú ý (tồn kho <= 5 hoặc sắp hết hạn)!", lowStock.size()),
                    "Cảnh báo hàng hóa", JOptionPane.WARNING_MESSAGE);
        });

        // 9. ComboBox Sắp xếp (Dùng Lambda Expression và Comparator)
        view.getCboSort().addActionListener(e -> handleSort());

        // 10. Nút Xuất Excel/CSV danh sách kho
        view.getBtnExportCsv().addActionListener(e -> handleExportCsv());
    }

    private void handleExportCsv() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Kho hàng hiện tại chưa có sản phẩm nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File exportFile = new File("BaoCao_Kho_Hang.csv");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(exportFile), StandardCharsets.UTF_8))) {
            writer.write("\uFEFF"); // UTF-8 BOM để Excel hiển thị đúng dấu tiếng Việt
            writer.write("Mã SP,Tên Sản Phẩm,Danh Mục,Giá Nhập,Giá Bán,Tồn Kho,Lợi Nhuận/SP\n");
            for (Product p : products) {
                writer.write(String.format("\"%s\",\"%s\",\"%s\",%.0f,%.0f,%d,%.0f\n",
                        p.getId(),
                        p.getName(),
                        p.getCategory().getDisplayName(),
                        p.getImportPrice(),
                        p.getSellingPrice(),
                        p.getQuantity(),
                        p.calculateProfit()));
            }
            JOptionPane.showMessageDialog(view, "Đã xuất báo cáo kho hàng thành công ra tệp:\n" + exportFile.getAbsolutePath(),
                    "Xuất file thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadAllProducts() {
        List<Product> list = productService.getAllProducts();
        view.updateTable(list);
    }

    private void displayProductDetails(String id) {
        Product p = productService.getProductById(id);
        if (p == null) return;

        view.getTxtId().setText(p.getId());
        view.getTxtId().setEditable(false);
        view.getTxtName().setText(p.getName());
        view.getCboCategory().setSelectedItem(p.getCategory());
        view.getTxtImportPrice().setText(String.format("%.0f", p.getImportPrice()));
        view.getTxtSellingPrice().setText(String.format("%.0f", p.getSellingPrice()));
        view.getTxtQuantity().setText(String.valueOf(p.getQuantity()));

        view.getExtraCardLayout().show(view.getExtraCardPanel(), p.getCategory().name());

        if (p instanceof FoodProduct fp) {
            view.getTxtFoodNsx().setText(fp.getManufactureDate() != null ? fp.getManufactureDate().toString() : "");
            view.getTxtFoodHsd().setText(fp.getExpirationDate() != null ? fp.getExpirationDate().toString() : "");
        } else if (p instanceof BeverageProduct bp) {
            view.getTxtBevVolume().setText(String.valueOf(bp.getVolumeMl()));
            view.getChkBevAlcohol().setSelected(bp.isAlcoholic());
            view.getTxtBevHsd().setText(bp.getExpirationDate() != null ? bp.getExpirationDate().toString() : "");
        } else if (p instanceof ElectronicProduct ep) {
            view.getTxtElecWarranty().setText(String.valueOf(ep.getWarrantyMonths()));
            view.getTxtElecWatt().setText(String.valueOf(ep.getPowerWatt()));
        }
    }

    private void handleAddProduct() {
        try {
            Product p = buildProductFromInputs();
            productService.addProduct(p);
            JOptionPane.showMessageDialog(view, "Thêm sản phẩm mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadAllProducts();
            clearForm();
        } catch (DuplicateIdException | InvalidDataException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateProduct() {
        try {
            Product p = buildProductFromInputs();
            productService.updateProduct(p);
            JOptionPane.showMessageDialog(view, "Cập nhật sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadAllProducts();
        } catch (NotFoundException | InvalidDataException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteProduct() {
        String id = view.getTxtId().getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn sản phẩm cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc chắn muốn xóa sản phẩm mã [" + id + "]?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteProduct(id);
                JOptionPane.showMessageDialog(view, "Đã xóa sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadAllProducts();
                clearForm();
            } catch (NotFoundException ex) {
                JOptionPane.showMessageDialog(view, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearch() {
        String keyword = view.getTxtSearch().getText();
        int catIndex = view.getCboFilterCategory().getSelectedIndex();
        Category cat = null;
        if (catIndex == 1) cat = Category.FOOD;
        else if (catIndex == 2) cat = Category.BEVERAGE;
        else if (catIndex == 3) cat = Category.ELECTRONIC;

        List<Product> result = productService.searchAndFilter(keyword, cat);
        view.updateTable(result);
    }

    private void handleSort() {
        int sortIndex = view.getCboSort().getSelectedIndex();
        Comparator<Product> comparator;

        switch (sortIndex) {
            case 1 -> comparator = (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName());
            case 2 -> comparator = Comparator.comparingDouble(Product::getSellingPrice);
            case 3 -> comparator = (p1, p2) -> Double.compare(p2.getSellingPrice(), p1.getSellingPrice());
            case 4 -> comparator = (p1, p2) -> Integer.compare(p2.getQuantity(), p1.getQuantity());
            case 5 -> comparator = (p1, p2) -> Double.compare(p2.calculateProfit(), p1.calculateProfit());
            default -> comparator = (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName());
        }

        List<Product> sorted = productService.sortProducts(comparator);
        view.updateTable(sorted);
    }

    private Product buildProductFromInputs() throws InvalidDataException {
        String id = view.getTxtId().getText().trim();
        String name = view.getTxtName().getText().trim();
        Category cat = (Category) view.getCboCategory().getSelectedItem();

        if (id.isEmpty()) throw new InvalidDataException("Mã sản phẩm không được để trống!");
        if (name.isEmpty()) throw new InvalidDataException("Tên sản phẩm không được để trống!");

        double importPrice;
        double sellingPrice;
        int qty;

        try {
            importPrice = Double.parseDouble(view.getTxtImportPrice().getText().trim());
            sellingPrice = Double.parseDouble(view.getTxtSellingPrice().getText().trim());
            qty = Integer.parseInt(view.getTxtQuantity().getText().trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Giá nhập, giá bán và số lượng phải là các số hợp lệ!");
        }

        if (importPrice < 0 || sellingPrice < 0 || qty < 0) {
            throw new InvalidDataException("Giá hoặc số lượng không được nhỏ hơn 0!");
        }

        if (cat == Category.FOOD) {
            LocalDate nsx;
            LocalDate hsd;
            try {
                nsx = LocalDate.parse(view.getTxtFoodNsx().getText().trim(), DATE_FMT);
                hsd = LocalDate.parse(view.getTxtFoodHsd().getText().trim(), DATE_FMT);
            } catch (Exception e) {
                throw new InvalidDataException("Định dạng ngày (NSX/HSD) phải là yyyy-MM-dd (ví dụ: 2026-12-31)!");
            }
            return new FoodProduct(id, name, importPrice, sellingPrice, qty, nsx, hsd);
        } else if (cat == Category.BEVERAGE) {
            int vol;
            try {
                vol = Integer.parseInt(view.getTxtBevVolume().getText().trim());
            } catch (Exception e) {
                throw new InvalidDataException("Dung tích phải là số nguyên (ml)!");
            }
            boolean alc = view.getChkBevAlcohol().isSelected();
            LocalDate hsd = null;
            String hsdStr = view.getTxtBevHsd().getText().trim();
            if (!hsdStr.isEmpty()) {
                try {
                    hsd = LocalDate.parse(hsdStr, DATE_FMT);
                } catch (Exception e) {
                    throw new InvalidDataException("HSD đồ uống phải theo định dạng yyyy-MM-dd!");
                }
            }
            return new BeverageProduct(id, name, importPrice, sellingPrice, qty, vol, alc, hsd);
        } else {
            int war;
            int watt;
            try {
                war = Integer.parseInt(view.getTxtElecWarranty().getText().trim());
                watt = Integer.parseInt(view.getTxtElecWatt().getText().trim());
            } catch (Exception e) {
                throw new InvalidDataException("Thời gian bảo hành và công suất phải là số nguyên!");
            }
            return new ElectronicProduct(id, name, importPrice, sellingPrice, qty, war, watt);
        }
    }

    private void clearForm() {
        view.getTxtId().setText("");
        view.getTxtId().setEditable(true);
        view.getTxtName().setText("");
        view.getTxtImportPrice().setText("");
        view.getTxtSellingPrice().setText("");
        view.getTxtQuantity().setText("");
        view.getTblProducts().clearSelection();
    }
}

