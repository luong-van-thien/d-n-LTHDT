import javax.swing.*;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private boolean isAlertFiltering = false;

    public ProductController(ProductPanel view, ProductService productService) {
        this.view = view;
        this.productService = productService;

        initListeners();
        loadAllProducts();
        clearForm(); // Tự động điền Mã SP kế tiếp ngay khi khởi động
    }

    private void initListeners() {
        // 1. Sự kiện chọn dòng trên bảng -> Nạp dữ liệu vào form; khi hủy chọn -> làm mới form
        view.getTblProducts().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = view.getTblProducts().getSelectedRow();
                if (selectedRow >= 0 && selectedRow < view.getTblProducts().getRowCount()) {
                    Object val = view.getTblProducts().getValueAt(selectedRow, 0);
                    if (val != null) {
                        displayProductDetails(val.toString());
                    }
                } else if (selectedRow == -1) {
                    clearForm();
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

        // 7. Nút Quay lại tất cả sản phẩm (Làm mới bộ lọc)
        view.getBtnResetFilter().addActionListener(e -> resetToAllProducts());

        // 8. Nút Cảnh báo hàng tồn ít / sắp hết hạn (Hỗ trợ Toggle Bật/Tắt quay lại tức thì)
        view.getBtnAlertFilter().addActionListener(e -> {
            if (!isAlertFiltering) {
                List<Product> lowStock = productService.getLowStockProducts(5);
                List<Product> expiring = productService.getExpiringProducts();
                List<Product> alerts = new ArrayList<>(lowStock);
                for (Product p : expiring) {
                    if (!alerts.contains(p)) alerts.add(p);
                }
                view.updateTable(alerts);
                isAlertFiltering = true;
                view.getBtnAlertFilter().setText("Quay lại tất cả SP (" + alerts.size() + " cảnh báo)");
                JOptionPane.showMessageDialog(view,
                        String.format("Đã phát hiện %d sản phẩm cần chú ý (tồn kho <= 5 hoặc sắp hết hạn)!\n\n" +
                                "(Mẹo: Bạn có thể bấm lại nút này hoặc bấm 'Quay lại tất cả SP' bất cứ lúc nào để xem lại toàn bộ kho)",
                                alerts.size()),
                        "Cảnh báo hàng hóa", JOptionPane.WARNING_MESSAGE);
            } else {
                resetToAllProducts();
            }
        });

        // 9. ComboBox Sắp xếp (Dùng Lambda Expression và Comparator)
        view.getCboSort().addActionListener(e -> handleSort());

        // 10. Nút Xuất Excel/CSV danh sách kho
        view.getBtnExportCsv().addActionListener(e -> handleExportCsv());

        // 11. Nút Tạo Phiếu Nhập Hàng Tự Động
        view.getBtnCreateRestockOrder().addActionListener(e -> handleCreateRestockOrder());
    }

    private void handleCreateRestockOrder() {
        List<Product> lowStock = productService.getLowStockProducts(5);
        List<Product> expiring = productService.getExpiringProducts();
        List<Product> needRestock = new ArrayList<>(lowStock);
        for (Product p : expiring) {
            if (!needRestock.contains(p)) {
                needRestock.add(p);
            }
        }

        if (needRestock.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Kho hàng đang ở trạng thái an toàn!\nKhông có mặt hàng nào cần nhập bù hoặc xử lý date.",
                    "Kho Hàng An Toàn", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "PhieuNhapHang_" + timestamp + ".txt";
        File file = new File(fileName);

        double totalEstimatedCost = 0.0;
        int targetSafeStock = 50; // Đề xuất nhập bù lên mức an toàn 50 SP

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            writer.write("========================================================================================\n");
            writer.write("                            SIÊU THỊ MINI - PTIT HCM                            \n");
            writer.write("                               PHIẾU ĐỀ NGHỊ NHẬP HÀNG                                  \n");
            writer.write("========================================================================================\n");
            writer.write(String.format("Mã phiếu : PN-%s\n", timestamp));
            writer.write(String.format("Ngày lập : %s\n", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            writer.write("Đơn vị đề xuất: Bộ phận Quản trị Kho hàng & Hàng tồn\n");
            writer.write(String.format("Lý do lập: Có %d mặt hàng rơi vào trạng thái nguy cấp (tồn kho <= 5 hoặc cận hạn sử dụng).\n", needRestock.size()));
            writer.write("----------------------------------------------------------------------------------------\n");
            writer.write(String.format("%-8s %-25s %-12s %-10s %-14s %-15s\n",
                    "Mã SP", "Tên sản phẩm", "Tồn hiện tại", "Đề xuất bù", "Giá nhập (đ)", "Dự toán (đ)"));
            writer.write("----------------------------------------------------------------------------------------\n");

            for (Product p : needRestock) {
                int restockQty = Math.max(10, targetSafeStock - p.getQuantity());
                double cost = restockQty * p.getImportPrice();
                totalEstimatedCost += cost;

                String pName = p.getName();
                if (pName.length() > 24) pName = pName.substring(0, 22) + "..";

                writer.write(String.format("%-8s %-25s %-12d %-10d %-14s %-15s\n",
                        p.getId(), pName, p.getQuantity(), restockQty,
                        UIUtils.formatCurrency(p.getImportPrice()),
                        UIUtils.formatCurrency(cost)));
            }

            writer.write("----------------------------------------------------------------------------------------\n");
            writer.write(String.format("TỔNG CHI PHÍ DỰ TOÁN NHẬP KHO: %,.0f VNĐ\n", totalEstimatedCost));
            writer.write("========================================================================================\n");
            writer.write("   NGƯỜI LẬP PHIẾU                  THỦ KHO PHÊ DUYỆT                 BAN GIÁM ĐỐC      \n");
            writer.write("      (Ký & ghi rõ họ tên)             (Ký & ghi rõ họ tên)               (Đã duyệt)    \n\n\n\n");

            int opt = JOptionPane.showConfirmDialog(view,
                    String.format("Đã tạo Phiếu nhập hàng thành công cho %d mặt hàng cần bổ sung!\nTổng dự toán kinh phí: %s\n\nBạn có muốn mở file phiếu nhập hàng vừa tạo không?",
                            needRestock.size(), UIUtils.formatCurrency(totalEstimatedCost)),
                    "Tạo Phiếu Nhập Hàng Thành Công", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (opt == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tạo phiếu nhập hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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
                    "Xuất tệp thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xuất tệp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            view.getDateFoodNsx().setLocalDate(fp.getManufactureDate());
            view.getDateFoodHsd().setLocalDate(fp.getExpirationDate());
        } else if (p instanceof BeverageProduct bp) {
            view.getTxtBevVolume().setText(String.valueOf(bp.getVolumeMl()));
            view.getChkBevAlcohol().setSelected(bp.isAlcoholic());
            view.getDateBevHsd().setLocalDate(bp.getExpirationDate());
        } else if (p instanceof ElectronicProduct ep) {
            view.getTxtElecWarranty().setText(String.valueOf(ep.getWarrantyMonths()));
            view.getTxtElecWatt().setText(String.valueOf(ep.getPowerWatt()));
        }
    }

    private void handleAddProduct() {
        String currentId = view.getTxtId().getText().trim();

        // Xử lý thông minh: Nếu người dùng đang chọn 1 sản phẩm có sẵn trên bảng (hoặc ô Mã SP đã tồn tại)
        // mà bấm 'Thêm mới' -> Tự động chuyển sang chế độ Thêm mới với Mã SP kế tiếp, tránh lỗi trùng mã!
        if (!view.getTxtId().isEditable() || productService.getProductById(currentId) != null) {
            clearForm();
            JOptionPane.showMessageDialog(view,
                    "Hệ thống đã tự động chuyển sang chế độ Thêm mới!\n" +
                    "Mã sản phẩm mới được sinh tự động: [" + view.getTxtId().getText() + "]\n\n" +
                    "Vui lòng nhập Tên sản phẩm, Giá bán và bấm 'Thêm mới' một lần nữa để lưu vào kho.",
                    "Sẵn sàng thêm sản phẩm mới", JOptionPane.INFORMATION_MESSAGE);
            view.getTxtName().requestFocus();
            return;
        }

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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi khi xóa sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearch() {
        isAlertFiltering = false;
        view.getBtnAlertFilter().setText("Cảnh báo tồn/hạn");

        String keyword = view.getTxtSearch().getText();
        int catIndex = view.getCboFilterCategory().getSelectedIndex();
        Category cat = null;
        if (catIndex == 1) cat = Category.FOOD;
        else if (catIndex == 2) cat = Category.BEVERAGE;
        else if (catIndex == 3) cat = Category.ELECTRONIC;

        List<Product> result = productService.searchAndFilter(keyword, cat);
        view.updateTable(result);
    }

    public void resetToAllProducts() {
        isAlertFiltering = false;
        view.getBtnAlertFilter().setText("Cảnh báo tồn/hạn");
        view.getTxtSearch().setText("");
        view.getCboFilterCategory().setSelectedIndex(0);
        view.getCboSort().setSelectedIndex(0);
        loadAllProducts();
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

        if (id.isEmpty()) throw new InvalidDataException("Vui lòng không để trống Mã sản phẩm!");
        if (name.isEmpty()) throw new InvalidDataException("Vui lòng không để trống Tên sản phẩm!");
        if (cat == null) throw new InvalidDataException("Vui lòng chọn Loại danh mục sản phẩm!");

        String importPriceStr = view.getTxtImportPrice().getText().trim();
        String sellingPriceStr = view.getTxtSellingPrice().getText().trim();
        String qtyStr = view.getTxtQuantity().getText().trim();

        if (importPriceStr.isEmpty()) throw new InvalidDataException("Vui lòng nhập Giá nhập hàng!");
        if (sellingPriceStr.isEmpty()) throw new InvalidDataException("Vui lòng nhập Giá bán sản phẩm!");
        if (qtyStr.isEmpty()) throw new InvalidDataException("Vui lòng nhập Số lượng tồn kho!");

        double importPrice;
        double sellingPrice;
        int qty;

        try {
            importPrice = Double.parseDouble(importPriceStr);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Giá nhập phải là chữ số hợp lệ (ví dụ: 15000), không được chứa ký tự chữ!");
        }

        try {
            sellingPrice = Double.parseDouble(sellingPriceStr);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Giá bán phải là chữ số hợp lệ (ví dụ: 25000), không được chứa ký tự chữ!");
        }

        try {
            qty = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Số lượng tồn kho phải là số nguyên hợp lệ (ví dụ: 50)!");
        }

        if (importPrice < 0) {
            throw new InvalidDataException("Giá nhập không thể là số âm!");
        }
        if (sellingPrice < 0) {
            throw new InvalidDataException("Giá bán không thể là số âm!");
        }
        if (sellingPrice < importPrice) {
            throw new InvalidDataException(String.format(
                    "Giá bán (%s) đang thấp hơn giá nhập (%s)! Cửa hàng sẽ bị lỗ kinh doanh, vui lòng kiểm tra lại!",
                    UIUtils.formatCurrency(sellingPrice), UIUtils.formatCurrency(importPrice)));
        }
        if (qty < 0) {
            throw new InvalidDataException("Số lượng tồn kho không thể là số âm!");
        }

        if (cat == Category.FOOD) {
            LocalDate nsx = view.getDateFoodNsx().getLocalDate();
            LocalDate hsd = view.getDateFoodHsd().getLocalDate();

            if (nsx == null) throw new InvalidDataException("Vui lòng chọn Ngày sản xuất (NSX) cho thực phẩm!");
            if (hsd == null) throw new InvalidDataException("Vui lòng chọn Hạn sử dụng (HSD) cho thực phẩm!");

            if (hsd.isBefore(nsx)) {
                throw new InvalidDataException("Hạn sử dụng (HSD) không thể trước Ngày sản xuất (NSX)!");
            }

            return new FoodProduct(id, name, importPrice, sellingPrice, qty, nsx, hsd);

        } else if (cat == Category.BEVERAGE) {
            String volStr = view.getTxtBevVolume().getText().trim();
            if (volStr.isEmpty()) throw new InvalidDataException("Vui lòng nhập Dung tích (ml) của đồ uống!");

            int vol;
            try {
                vol = Integer.parseInt(volStr);
            } catch (Exception e) {
                throw new InvalidDataException("Dung tích đồ uống phải là số nguyên (ml)!");
            }
            if (vol <= 0) {
                throw new InvalidDataException("Dung tích đồ uống phải lớn hơn 0 ml!");
            }

            boolean alc = view.getChkBevAlcohol().isSelected();
            LocalDate hsd = view.getDateBevHsd().getLocalDate();
            return new BeverageProduct(id, name, importPrice, sellingPrice, qty, vol, alc, hsd);

        } else {
            String warStr = view.getTxtElecWarranty().getText().trim();
            String wattStr = view.getTxtElecWatt().getText().trim();

            if (warStr.isEmpty()) throw new InvalidDataException("Vui lòng nhập Thời gian bảo hành (tháng)!");
            if (wattStr.isEmpty()) throw new InvalidDataException("Vui lòng nhập Công suất tiêu thụ (Watt)!");

            int war;
            int watt;
            try {
                war = Integer.parseInt(warStr);
            } catch (Exception e) {
                throw new InvalidDataException("Thời gian bảo hành phải là số nguyên (tháng)!");
            }
            try {
                watt = Integer.parseInt(wattStr);
            } catch (Exception e) {
                throw new InvalidDataException("Công suất phải là số nguyên (Watt)!");
            }

            if (war < 0) throw new InvalidDataException("Thời gian bảo hành không được là số âm!");
            if (watt < 0) throw new InvalidDataException("Công suất không được là số âm!");

            return new ElectronicProduct(id, name, importPrice, sellingPrice, qty, war, watt);
        }
    }

    private void clearForm() {
        String nextId = productService.generateNextId();
        view.getTxtId().setText(nextId);
        view.getTxtId().setEditable(true);
        view.getTxtName().setText("");
        view.getTxtImportPrice().setText("");
        view.getTxtSellingPrice().setText("");
        view.getTxtQuantity().setText("50");
        view.getDateFoodNsx().setLocalDate(LocalDate.now().minusMonths(1));
        view.getDateFoodHsd().setLocalDate(LocalDate.now().plusMonths(6));
        view.getDateBevHsd().setLocalDate(LocalDate.now().plusMonths(9));
        view.getTxtBevVolume().setText("330");
        view.getChkBevAlcohol().setSelected(false);
        view.getTxtElecWarranty().setText("12");
        view.getTxtElecWatt().setText("1000");
        view.getTblProducts().clearSelection();
        view.getTxtName().requestFocus();
    }
}

