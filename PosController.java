import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Desktop;
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
        // 1. Tìm kiếm sản phẩm bán
        view.getTxtSearchProduct().addActionListener(e -> handleSearchProduct());
        view.getTxtSearchProduct().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { handleSearchProduct(); }
            @Override public void removeUpdate(DocumentEvent e) { handleSearchProduct(); }
            @Override public void changedUpdate(DocumentEvent e) { handleSearchProduct(); }
        });

        // 2. Thêm vào giỏ hàng
        view.getBtnAddToCart().addActionListener(e -> handleAddToCart());

        // 3. Xóa sản phẩm khỏi giỏ
        view.getBtnRemoveFromCart().addActionListener(e -> handleRemoveFromCart());

        // 4. Xóa trắng giỏ hàng
        view.getBtnClearCart().addActionListener(e -> {
            currentCart.clear();
            view.updateCartTable(currentCart);
            recalculateSummary();
        });

        // 5. Thay đổi khách hàng -> tính lại chiết khấu
        view.getCboCustomer().addActionListener(e -> {
            Customer selected = (Customer) view.getCboCustomer().getSelectedItem();
            if (selected == null) {
                view.getLblCustomerType().setText("Khách vãng lai (Giảm 0%)");
            } else {
                view.getLblCustomerType().setText(String.format("%s (Giảm %.0f%%)",
                        selected.getCustomerType(), selected.getDiscountRate() * 100));
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

    private void handleSearchProduct() {
        String keyword = view.getTxtSearchProduct().getText().trim();
        List<Product> filtered = productService.searchAndFilter(keyword, null);
        view.updateAvailableProducts(filtered);
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

