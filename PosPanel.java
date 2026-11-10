import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Quầy Thu Ngân & Bán hàng (PosPanel).
 */
public class PosPanel extends JPanel {
    // 1. Left: Product Selection
    private JTable tblAvailableProducts;
    private DefaultTableModel modelAvailableProducts;
    private JTextField txtSearchProduct;
    private JSpinner spinQuantity;
    private JButton btnAddToCart;

    // 2. Center: Shopping Cart
    private JTable tblCart;
    private DefaultTableModel modelCart;
    private JButton btnRemoveFromCart;
    private JButton btnClearCart;

    // 3. Right: Checkout & Customer Info
    private JComboBox<Customer> cboCustomer;
    private JButton btnNewCustomer;
    private JLabel lblCustomerType;
    private JLabel lblTotalAmount;
    private JLabel lblDiscountAmount;
    private JLabel lblFinalAmount;
    private JTextField txtCashGiven;
    private JLabel lblReturnCash;
    private JButton btnCheckout;

    public PosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        // --- 1. LEFT PANEL: Danh sách sản phẩm có thể bán ---
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(360, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "1. CHỌN SẢN PHẨM", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));

        JPanel leftTop = new JPanel(new BorderLayout(5, 5));
        leftTop.setBackground(Color.WHITE);
        leftTop.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftTop.add(new JLabel("Tìm SP: "), BorderLayout.WEST);
        txtSearchProduct = new JTextField();
        leftTop.add(txtSearchProduct, BorderLayout.CENTER);
        leftPanel.add(leftTop, BorderLayout.NORTH);

        String[] pCols = {"Mã SP", "Tên sản phẩm", "Giá bán", "Kho"};
        modelAvailableProducts = new DefaultTableModel(pCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblAvailableProducts = new JTable(modelAvailableProducts);
        tblAvailableProducts.setFont(UIUtils.BODY_FONT);
        tblAvailableProducts.setRowHeight(24);
        tblAvailableProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblAvailableProducts.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblAvailableProducts.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        leftPanel.add(new JScrollPane(tblAvailableProducts), BorderLayout.CENTER);

        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        leftBottom.setBackground(Color.WHITE);
        leftBottom.add(new JLabel("Số lượng mua:"));
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        ((JSpinner.DefaultEditor) spinQuantity.getEditor()).getTextField().setColumns(3);
        leftBottom.add(spinQuantity);

        btnAddToCart = UIUtils.createStyledButton("Thêm vào giỏ ➔", UIUtils.PRIMARY_COLOR, Color.WHITE);
        leftBottom.add(btnAddToCart);
        leftPanel.add(leftBottom, BorderLayout.SOUTH);

        // --- 2. CENTER PANEL: Giỏ hàng ---
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "2. GIỎ HÀNG HIỆN TẠI", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));

        String[] cartCols = {"Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
        modelCart = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblCart = new JTable(modelCart);
        tblCart.setFont(UIUtils.BODY_FONT);
        tblCart.setRowHeight(26);
        tblCart.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblCart.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblCart.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        centerPanel.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        JPanel centerBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        centerBottom.setBackground(Color.WHITE);
        btnRemoveFromCart = UIUtils.createStyledButton("❌ Xóa sản phẩm", UIUtils.DANGER_COLOR, Color.WHITE);
        btnClearCart = UIUtils.createStyledButton("🗑️ Làm trống giỏ", new Color(108, 117, 125), Color.WHITE);
        centerBottom.add(btnRemoveFromCart);
        centerBottom.add(btnClearCart);
        centerPanel.add(centerBottom, BorderLayout.SOUTH);

        // --- 3. RIGHT PANEL: Thông tin khách hàng & Thanh toán ---
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(320, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "3. THANH TOÁN", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));

        JPanel payForm = new JPanel(new GridBagLayout());
        payForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        payForm.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cboCustomer = new JComboBox<>();
        payForm.add(cboCustomer, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        btnNewCustomer = new JButton("➕ Tạo khách hàng mới");
        btnNewCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        payForm.add(btnNewCustomer, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        payForm.add(new JLabel("Loại KH:"), gbc);
        gbc.gridx = 1;
        lblCustomerType = new JLabel("Khách lẻ (Giảm 0%)");
        lblCustomerType.setFont(UIUtils.BOLD_FONT);
        lblCustomerType.setForeground(new Color(13, 110, 253));
        payForm.add(lblCustomerType, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        payForm.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4;
        payForm.add(new JLabel("Tổng tiền hàng:"), gbc);
        gbc.gridx = 1;
        lblTotalAmount = new JLabel("0 đ", JLabel.RIGHT);
        lblTotalAmount.setFont(UIUtils.BOLD_FONT);
        payForm.add(lblTotalAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        payForm.add(new JLabel("Chiết khấu:"), gbc);
        gbc.gridx = 1;
        lblDiscountAmount = new JLabel("0 đ", JLabel.RIGHT);
        lblDiscountAmount.setFont(UIUtils.BOLD_FONT);
        lblDiscountAmount.setForeground(UIUtils.DANGER_COLOR);
        payForm.add(lblDiscountAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblPayTitle = new JLabel("CẦN THANH TOÁN:");
        lblPayTitle.setFont(UIUtils.BOLD_FONT);
        payForm.add(lblPayTitle, gbc);
        gbc.gridx = 1;
        lblFinalAmount = new JLabel("0 đ", JLabel.RIGHT);
        lblFinalAmount.setFont(UIUtils.BIG_PRICE_FONT);
        lblFinalAmount.setForeground(UIUtils.SUCCESS_COLOR);
        payForm.add(lblFinalAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        payForm.add(new JLabel("Tiền khách đưa:"), gbc);
        gbc.gridx = 1;
        txtCashGiven = new JTextField("0");
        txtCashGiven.setFont(UIUtils.BOLD_FONT);
        txtCashGiven.setHorizontalAlignment(JTextField.RIGHT);
        payForm.add(txtCashGiven, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        payForm.add(new JLabel("Tiền thừa trả lại:"), gbc);
        gbc.gridx = 1;
        lblReturnCash = new JLabel("0 đ", JLabel.RIGHT);
        lblReturnCash.setFont(UIUtils.BOLD_FONT);
        lblReturnCash.setForeground(new Color(0, 102, 204));
        payForm.add(lblReturnCash, gbc);

        rightPanel.add(payForm, BorderLayout.CENTER);

        btnCheckout = UIUtils.createStyledButton("💳 THANH TOÁN & IN HÓA ĐƠN", UIUtils.SUCCESS_COLOR, Color.WHITE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.setPreferredSize(new Dimension(0, 50));
        rightPanel.add(btnCheckout, BorderLayout.SOUTH);

        JSplitPane splitLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
        splitLeft.setDividerLocation(360);
        splitLeft.setResizeWeight(0.4);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitLeft, rightPanel);
        mainSplit.setDividerLocation(780);
        mainSplit.setResizeWeight(0.7);

        add(mainSplit, BorderLayout.CENTER);
    }

    public void updateAvailableProducts(List<Product> products) {
        modelAvailableProducts.setRowCount(0);
        for (Product p : products) {
            modelAvailableProducts.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    UIUtils.formatCurrency(p.getSellingPrice()),
                    p.getQuantity()
            });
        }
    }

    public void updateCartTable(List<InvoiceItem> items) {
        modelCart.setRowCount(0);
        for (InvoiceItem item : items) {
            modelCart.addRow(new Object[]{
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    UIUtils.formatCurrency(item.getUnitPrice()),
                    item.getQuantity(),
                    UIUtils.formatCurrency(item.getSubTotal())
            });
        }
    }

    public void setCustomerList(List<Customer> customers) {
        cboCustomer.removeAllItems();
        cboCustomer.addItem(null); // Khách lẻ / Vãng lai
        for (Customer c : customers) {
            cboCustomer.addItem(c);
        }
    }

    // Getters
    public JTable getTblAvailableProducts() { return tblAvailableProducts; }
    public JTextField getTxtSearchProduct() { return txtSearchProduct; }
    public JSpinner getSpinQuantity() { return spinQuantity; }
    public JButton getBtnAddToCart() { return btnAddToCart; }
    public JTable getTblCart() { return tblCart; }
    public JButton getBtnRemoveFromCart() { return btnRemoveFromCart; }
    public JButton getBtnClearCart() { return btnClearCart; }
    public JComboBox<Customer> getCboCustomer() { return cboCustomer; }
    public JButton getBtnNewCustomer() { return btnNewCustomer; }
    public JLabel getLblCustomerType() { return lblCustomerType; }
    public JLabel getLblTotalAmount() { return lblTotalAmount; }
    public JLabel getLblDiscountAmount() { return lblDiscountAmount; }
    public JLabel getLblFinalAmount() { return lblFinalAmount; }
    public JTextField getTxtCashGiven() { return txtCashGiven; }
    public JLabel getLblReturnCash() { return lblReturnCash; }
    public JButton getBtnCheckout() { return btnCheckout; }
}

