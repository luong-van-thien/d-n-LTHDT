import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Điều khiển Nghiệp vụ Bán hàng (PosController).
 */
public class PosController {
    private final PosPanel view;
    private final ProductService productService;
    private final CustomerRepository customerRepo;
    private final OrderService orderService;
    private final Runnable onDataChangedCallback;

    private final List<InvoiceItem> currentCart = new ArrayList<>();
    private double currentTotalAmount = 0.0;
    private double currentDiscountAmount = 0.0;
    private double currentFinalAmount = 0.0;

    // Hàng đợi lưu tạm các hóa đơn bị treo (Hold Bills)
    private static class HeldBill {
        final Customer customer;
        final List<InvoiceItem> items;
        final java.time.LocalDateTime time;

        HeldBill(Customer customer, List<InvoiceItem> items) {
            this.customer = customer;
            this.items = items;
            this.time = java.time.LocalDateTime.now();
        }
    }
    private final List<HeldBill> heldBills = new ArrayList<>();

    public PosController(PosPanel view, ProductService productService,
                         CustomerRepository customerRepo, OrderService orderService,
                         Runnable onDataChangedCallback) {
        this.view = view;
        this.productService = productService;
        this.customerRepo = customerRepo;
        this.orderService = orderService;
        this.onDataChangedCallback = onDataChangedCallback;

        initListeners();
        loadAvailableProducts();
        loadCustomers();
    }

    private void initListeners() {
        // 1. Quét mã vạch Barcode (phím Enter) & Tìm kiếm sản phẩm
        view.getTxtSearchProduct().addActionListener(e -> handleBarcodeOrSearch());
        view.getTxtSearchProduct().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { handleSearchProduct(); }
            @Override public void removeUpdate(DocumentEvent e) { handleSearchProduct(); }
            @Override public void changedUpdate(DocumentEvent e) { handleSearchProduct(); }
        });
        view.getCboCategoryFilter().addActionListener(e -> handleSearchProduct());

        // Nhấp đúp chuột vào bảng sản phẩm để thêm nhanh 1 SP vào giỏ
        view.getTblAvailableProducts().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && view.getTblAvailableProducts().getSelectedRow() >= 0) {
                    handleQuickAddOne();
                }
            }
        });

        // 2. Thêm vào giỏ hàng bằng nút
        view.getBtnAddToCart().addActionListener(e -> handleAddToCart());

        // 3. Tăng/Giảm/Xóa món trong giỏ
        view.getBtnIncreaseQty().addActionListener(e -> handleIncreaseCartQty());
        view.getBtnDecreaseQty().addActionListener(e -> handleDecreaseCartQty());
        view.getBtnRemoveFromCart().addActionListener(e -> handleRemoveFromCart());

        // 4. Xóa trắng giỏ hàng & Treo / Mở đơn hàng tạm (Hold / Resume Bill)
        view.getBtnClearCart().addActionListener(e -> {
            currentCart.clear();
            view.updateCartTable(currentCart);
            recalculateSummary();
        });
        view.getBtnHoldBill().addActionListener(e -> handleHoldBill());
        view.getBtnResumeBill().addActionListener(e -> handleResumeBill());

        // 5. Thay đổi khách hàng -> tính lại chiết khấu & điểm thưởng
        view.getCboCustomer().addActionListener(e -> {
            Customer selected = (Customer) view.getCboCustomer().getSelectedItem();
            if (selected == null) {
                view.getLblCustomerType().setText("Khách lẻ (Giảm 0%)");
                view.getLblCustomerPoints().setText("Điểm: 0");
            } else {
                view.getLblCustomerType().setText(String.format("%s (Giảm %.0f%%)",
                        selected.getCustomerType(), selected.getDiscountRate() * 100));
                view.getLblCustomerPoints().setText(String.format("Điểm: %d", selected.getLoyaltyPoints()));
            }
            recalculateSummary();
        });

        // 6. Tạo khách hàng mới nhanh
        view.getBtnNewCustomer().addActionListener(e -> handleCreateNewCustomer());

        // 7. Nhập tiền khách đưa -> tự tính tiền thối lại
        view.getTxtCashGiven().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateReturnCash(); }
            @Override public void removeUpdate(DocumentEvent e) { updateReturnCash(); }
            @Override public void changedUpdate(DocumentEvent e) { updateReturnCash(); }
        });

        // Hàng nút chọn nhanh tiền mặt (Quick Cash Buttons)
        view.getBtnExactCash().addActionListener(e -> view.getTxtCashGiven().setText(String.format(java.util.Locale.US, "%.0f", currentFinalAmount)));
        view.getBtnCash50k().addActionListener(e -> view.getTxtCashGiven().setText("50000"));
        view.getBtnCash100k().addActionListener(e -> view.getTxtCashGiven().setText("100000"));
        view.getBtnCash200k().addActionListener(e -> view.getTxtCashGiven().setText("200000"));
        view.getBtnCash500k().addActionListener(e -> view.getTxtCashGiven().setText("500000"));

        // Hủy đơn / Làm mới quầy
        view.getBtnCancelOrder().addActionListener(e -> {
            if (!currentCart.isEmpty()) {
                int opt = JOptionPane.showConfirmDialog(view, "Bạn có muốn hủy toàn bộ giỏ hàng và làm mới quầy không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (opt != JOptionPane.YES_OPTION) return;
            }
            currentCart.clear();
            view.updateCartTable(currentCart);
            view.getTxtCashGiven().setText("0");
            recalculateSummary();
        });

        // 8. Nút Thanh toán & In hóa đơn
        view.getBtnCheckout().addActionListener(e -> handleCheckout());
    }

    public void loadAvailableProducts() {
        List<Product> products = productService.getAllProducts();
        view.updateAvailableProducts(products);
    }

    public void loadCustomers() {
        view.setCustomerList(customerRepo.getAll());
    }

    /**
     * Xử lý khi nhấn phím Enter trên ô tìm kiếm:
     * Hỗ trợ tương thích phần cứng súng quét mã vạch (Barcode Scanner).
     * Súng quét mã vạch sẽ nhập chuỗi mã SP + tự động gửi ký tự Enter.
     */
    private void handleBarcodeOrSearch() {
        String code = view.getTxtSearchProduct().getText().trim();
        if (code.isEmpty()) return;

        Product product = productService.getProductById(code);
        if (product != null) {
            // Đúng mã SP (Barcode) -> Thêm ngay 1 món vào giỏ & phát tiếng bíp
            addProductDirectlyToCart(product);
            Toolkit.getDefaultToolkit().beep(); // Âm thanh bíp quét mã vạch
            view.getTxtSearchProduct().setText("");
            SwingUtilities.invokeLater(() -> view.getTxtSearchProduct().requestFocusInWindow());
            return;
        }

        handleSearchProduct();
    }

    private void addProductDirectlyToCart(Product product) {
        InvoiceItem existingItem = null;
        for (InvoiceItem item : currentCart) {
            if (item.getProduct().getId().equalsIgnoreCase(product.getId())) {
                existingItem = item;
                break;
            }
        }

        int currentQtyInCart = (existingItem != null) ? existingItem.getQuantity() : 0;
        if (currentQtyInCart + 1 > product.getQuantity()) {
            JOptionPane.showMessageDialog(view,
                    String.format("Kho chỉ còn %d sản phẩm '%s'!", product.getQuantity(), product.getName()),
                    "Hết hàng trong kho", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            currentCart.add(new InvoiceItem(product, 1, product.getSellingPrice()));
        }

        view.updateCartTable(currentCart);
        recalculateSummary();
    }

    private void handleSearchProduct() {
        String keyword = view.getTxtSearchProduct().getText().trim();
        Category category = null;
        int filterIndex = view.getCboCategoryFilter().getSelectedIndex();
        if (filterIndex == 1) category = Category.FOOD;
        else if (filterIndex == 2) category = Category.BEVERAGE;
        else if (filterIndex == 3) category = Category.ELECTRONIC;

        List<Product> filtered = productService.searchAndFilter(keyword, category);
        view.updateAvailableProducts(filtered);
    }

    private void handleQuickAddOne() {
        int row = view.getTblAvailableProducts().getSelectedRow();
        if (row < 0) return;

        String productId = (String) view.getTblAvailableProducts().getValueAt(row, 0);
        Product product = productService.getProductById(productId);
        if (product == null) return;

        InvoiceItem existingItem = null;
        for (InvoiceItem item : currentCart) {
            if (item.getProduct().getId().equals(productId)) {
                existingItem = item;
                break;
            }
        }

        int currentQtyInCart = (existingItem != null) ? existingItem.getQuantity() : 0;
        if (currentQtyInCart + 1 > product.getQuantity()) {
            JOptionPane.showMessageDialog(view,
                    String.format("Kho chỉ còn %d sản phẩm '%s'!", product.getQuantity(), product.getName()),
                    "Không đủ hàng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            currentCart.add(new InvoiceItem(product, 1, product.getSellingPrice()));
        }

        view.updateCartTable(currentCart);
        recalculateSummary();
    }

    private void handleIncreaseCartQty() {
        int row = view.getTblCart().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn 1 mặt hàng trong giỏ để tăng số lượng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        InvoiceItem item = currentCart.get(row);
        Product p = productService.getProductById(item.getProduct().getId());
        if (p == null) return;

        if (item.getQuantity() + 1 > p.getQuantity()) {
            JOptionPane.showMessageDialog(view, "Kho chỉ còn " + p.getQuantity() + " sản phẩm!", "Không đủ hàng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        item.setQuantity(item.getQuantity() + 1);
        view.updateCartTable(currentCart);
        view.getTblCart().setRowSelectionInterval(row, row);
        recalculateSummary();
    }

    private void handleDecreaseCartQty() {
        int row = view.getTblCart().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn 1 mặt hàng trong giỏ để giảm số lượng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        InvoiceItem item = currentCart.get(row);
        if (item.getQuantity() - 1 <= 0) {
            currentCart.remove(row);
            view.updateCartTable(currentCart);
        } else {
            item.setQuantity(item.getQuantity() - 1);
            view.updateCartTable(currentCart);
            view.getTblCart().setRowSelectionInterval(row, row);
        }
        recalculateSummary();
    }

    /**
     * Nghiệp vụ Treo Đơn (Hold Bill):
     * Lưu tạm đơn hàng của khách hiện tại vào hàng đợi để phục vụ khách tiếp theo.
     */
    private void handleHoldBill() {
        if (currentCart.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Giỏ hàng đang trống, không thể treo đơn!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer cust = (Customer) view.getCboCustomer().getSelectedItem();
        List<InvoiceItem> copyItems = new ArrayList<>();
        for (InvoiceItem item : currentCart) {
            copyItems.add(new InvoiceItem(item.getProduct(), item.getQuantity(), item.getUnitPrice()));
        }

        heldBills.add(new HeldBill(cust, copyItems));
        currentCart.clear();
        view.updateCartTable(currentCart);
        view.getTxtCashGiven().setText("0");
        recalculateSummary();
        updateResumeButtonText();

        JOptionPane.showMessageDialog(view,
                String.format("Đã lưu tạm đơn hàng của khách [%s] vào hàng đợi!\nQuầy sẵn sàng tính tiền cho khách tiếp theo.",
                        cust != null ? cust.getName() : "Khách lẻ"),
                "Treo Đơn Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Nghiệp vụ Mở Đơn Treo (Resume Bill):
     * Mở lại hóa đơn đã lưu tạm khi khách quay lại quầy.
     */
    private void handleResumeBill() {
        if (heldBills.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Hiện không có đơn hàng nào đang treo!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!currentCart.isEmpty()) {
            int opt = JOptionPane.showConfirmDialog(view,
                    "Giỏ hàng hiện tại đang có sản phẩm. Bạn có muốn lưu tạm đơn này trước khi mở đơn khác không?",
                    "Xác nhận", JOptionPane.YES_NO_CANCEL_OPTION);
            if (opt == JOptionPane.CANCEL_OPTION) return;
            if (opt == JOptionPane.YES_OPTION) {
                handleHoldBill();
            } else {
                currentCart.clear();
            }
        }

        String[] options = new String[heldBills.size()];
        for (int i = 0; i < heldBills.size(); i++) {
            HeldBill hb = heldBills.get(i);
            String cName = hb.customer != null ? hb.customer.getName() : "Khách lẻ";
            double total = hb.items.stream().mapToDouble(InvoiceItem::getSubTotal).sum();
            options[i] = String.format("Đơn #%d: %s | %d món | %s (Lưu lúc: %s)",
                    i + 1, cName, hb.items.size(), UIUtils.formatCurrency(total),
                    hb.time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        }

        String chosen = (String) JOptionPane.showInputDialog(view,
                "Chọn đơn hàng đã treo cần mở lại để thanh toán:",
                "Khôi Phục Đơn Treo",
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (chosen != null) {
            int idx = -1;
            for (int i = 0; i < options.length; i++) {
                if (options[i].equals(chosen)) {
                    idx = i;
                    break;
                }
            }
            if (idx >= 0) {
                HeldBill restored = heldBills.remove(idx);
                currentCart.clear();
                currentCart.addAll(restored.items);
                view.updateCartTable(currentCart);
                view.getCboCustomer().setSelectedItem(restored.customer);
                recalculateSummary();
                updateResumeButtonText();
            }
        }
    }

    private void updateResumeButtonText() {
        view.getBtnResumeBill().setText("Mở đơn treo (" + heldBills.size() + ")");
    }

    private void handleAddToCart() {
        int row = view.getTblAvailableProducts().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn 1 sản phẩm từ danh sách bên trái!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productId = (String) view.getTblAvailableProducts().getValueAt(row, 0);
        Product product = productService.getProductById(productId);
        if (product == null) return;

        int buyQty = (int) view.getSpinQuantity().getValue();
        if (buyQty <= 0) {
            JOptionPane.showMessageDialog(view, "Số lượng mua phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        InvoiceItem existingItem = null;
        for (InvoiceItem item : currentCart) {
            if (item.getProduct().getId().equals(productId)) {
                existingItem = item;
                break;
            }
        }

        int currentQtyInCart = (existingItem != null) ? existingItem.getQuantity() : 0;
        int totalRequested = currentQtyInCart + buyQty;

        if (totalRequested > product.getQuantity()) {
            JOptionPane.showMessageDialog(view,
                    String.format("Kho chỉ còn %d sản phẩm (trong giỏ đã có %d)!", product.getQuantity(), currentQtyInCart),
                    "Không đủ hàng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existingItem != null) {
            existingItem.setQuantity(totalRequested);
        } else {
            currentCart.add(new InvoiceItem(product, buyQty, product.getSellingPrice()));
        }

        view.updateCartTable(currentCart);
        recalculateSummary();
    }

    private void handleRemoveFromCart() {
        int row = view.getTblCart().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn sản phẩm trong giỏ để xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        currentCart.remove(row);
        view.updateCartTable(currentCart);
        recalculateSummary();
    }

    private void recalculateSummary() {
        currentTotalAmount = 0.0;
        for (InvoiceItem item : currentCart) {
            currentTotalAmount += item.getSubTotal();
        }

        Customer customer = (Customer) view.getCboCustomer().getSelectedItem();
        if (customer != null) {
            currentDiscountAmount = customer.calculateDiscount(currentTotalAmount);
        } else {
            currentDiscountAmount = 0.0;
        }

        currentFinalAmount = Math.max(0, currentTotalAmount - currentDiscountAmount);

        view.getLblTotalAmount().setText(UIUtils.formatCurrency(currentTotalAmount));
        view.getLblDiscountAmount().setText("- " + UIUtils.formatCurrency(currentDiscountAmount));
        view.getLblFinalAmount().setText(UIUtils.formatCurrency(currentFinalAmount));

        if (view.getTxtCashGiven().getText().isEmpty() || view.getTxtCashGiven().getText().equals("0")) {
            view.getTxtCashGiven().setText(String.format("%.0f", currentFinalAmount));
        }

        updateReturnCash();
    }

    private void updateReturnCash() {
        try {
            String text = view.getTxtCashGiven().getText().trim().replace(",", "").replace(".", "");
            if (text.isEmpty()) {
                view.getLblReturnCash().setText("0 đ");
                return;
            }
            double cash = Double.parseDouble(text);
            double change = cash - currentFinalAmount;
            if (change >= 0) {
                view.getLblReturnCash().setText(UIUtils.formatCurrency(change));
                view.getLblReturnCash().setForeground(new java.awt.Color(40, 167, 69));
            } else {
                view.getLblReturnCash().setText("Thiếu " + UIUtils.formatCurrency(Math.abs(change)));
                view.getLblReturnCash().setForeground(UIUtils.DANGER_COLOR);
            }
        } catch (NumberFormatException e) {
            view.getLblReturnCash().setText("Nhập số!");
        }
    }

    private void handleCreateNewCustomer() {
        JTextField txtId = new JTextField("KH" + (customerRepo.getAll().size() + 1));
        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JComboBox<String> cboType = new JComboBox<>(new String[]{"Khách Thường (Regular)", "Khách VIP (VIP)"});

        JPanel panel = new JPanel(new java.awt.GridLayout(4, 2, 8, 8));
        panel.add(new JLabel("Mã khách hàng:"));
        panel.add(txtId);
        panel.add(new JLabel("Họ và tên:"));
        panel.add(txtName);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(txtPhone);
        panel.add(new JLabel("Loại thành viên:"));
        panel.add(cboType);

        int result = JOptionPane.showConfirmDialog(view, panel, "Thêm Khách Hàng Thân Thiết", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ tên và số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer c;
            if (cboType.getSelectedIndex() == 1) {
                c = new VIPCustomer(id, name, phone, 0, 100);
            } else {
                c = new RegularCustomer(id, name, phone, 0);
            }

            customerRepo.save(c);
            loadCustomers();
            view.getCboCustomer().setSelectedItem(c);
            JOptionPane.showMessageDialog(view, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleCheckout() {
        if (currentCart.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Giỏ hàng đang trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double cashGiven;
        try {
            String text = view.getTxtCashGiven().getText().trim().replace(",", "").replace(".", "");
            cashGiven = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập số tiền khách đưa hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cashGiven < currentFinalAmount) {
            JOptionPane.showMessageDialog(view, "Tiền khách đưa chưa đủ để thanh toán!", "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = (Customer) view.getCboCustomer().getSelectedItem();

        try {
            Invoice invoice = orderService.checkout(customer, currentCart, cashGiven);

            int opt = JOptionPane.showConfirmDialog(view,
                    String.format("Thanh toán thành công hóa đơn [%s]!\nSố tiền: %s\nTiền thối lại: %s\n\nBạn có muốn mở file hóa đơn text vừa in không?",
                            invoice.getInvoiceId(),
                            UIUtils.formatCurrency(invoice.getFinalAmount()),
                            UIUtils.formatCurrency(invoice.getReturnCash())),
                    "Thanh toán thành công", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (opt == JOptionPane.YES_OPTION) {
                File file = new File(invoice.getInvoiceId() + ".txt");
                if (file.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            }

            if (orderService.getLastUpgradeMessage() != null) {
                JOptionPane.showMessageDialog(view, orderService.getLastUpgradeMessage(), "Nâng Hạng VIP!", JOptionPane.INFORMATION_MESSAGE);
            }

            currentCart.clear();
            view.updateCartTable(currentCart);
            loadAvailableProducts();
            loadCustomers();
            recalculateSummary();
            view.getTxtCashGiven().setText("0");

            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }

        } catch (InvalidDataException | OutOfStockException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

