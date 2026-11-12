import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Quầy Thu Ngân & Bán hàng (PosPanel).
 * Thiết kế giao diện hiện đại, chuyên nghiệp chuẩn hệ thống POS bán lẻ.
 */
public class PosPanel extends JPanel {
    // 1. Left: Product Selection
    private JTable tblAvailableProducts;
    private DefaultTableModel modelAvailableProducts;
    private JTextField txtSearchProduct;
    private JComboBox<String> cboCategoryFilter;
    private JSpinner spinQuantity;
    private JButton btnAddToCart;

    // 2. Center: Shopping Cart
    private JTable tblCart;
    private DefaultTableModel modelCart;
    private JButton btnIncreaseQty;
    private JButton btnDecreaseQty;
    private JButton btnRemoveFromCart;
    private JButton btnClearCart;
    private JButton btnHoldBill;
    private JButton btnResumeBill;
    private JLabel lblCartSummary;

    // 3. Right: Checkout & Customer Info
    private JComboBox<Customer> cboCustomer;
    private JButton btnNewCustomer;
    private JLabel lblCustomerType;
    private JLabel lblCustomerPoints;
    private JLabel lblTotalAmount;
    private JLabel lblDiscountAmount;
    private JLabel lblFinalAmount;
    private JTextField txtCashGiven;
    private JLabel lblReturnCash;
    private JButton btnCheckout;
    private JButton btnCancelOrder;

    // Quick cash buttons
    private JButton btnExactCash;
    private JButton btnCash50k;
    private JButton btnCash100k;
    private JButton btnCash200k;
    private JButton btnCash500k;

    public PosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        // =========================================================================
        // --- 1. LEFT PANEL: Danh sách sản phẩm có thể bán ---
        // =========================================================================
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(465, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "1. CHỌN SẢN PHẨM BÁN", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));

        // Thanh tìm kiếm và lọc danh mục
        JPanel leftTop = new JPanel(new GridLayout(2, 1, 4, 4));
        leftTop.setBackground(Color.WHITE);
        leftTop.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JPanel searchRow = new JPanel(new BorderLayout(6, 0));
        searchRow.setBackground(Color.WHITE);
        searchRow.add(new JLabel("Tìm SP:"), BorderLayout.WEST);
        txtSearchProduct = new JTextField();
        txtSearchProduct.setToolTipText("Gõ mã hoặc tên sản phẩm...");
        searchRow.add(txtSearchProduct, BorderLayout.CENTER);
        leftTop.add(searchRow);

        JPanel filterRow = new JPanel(new BorderLayout(6, 0));
        filterRow.setBackground(Color.WHITE);
        filterRow.add(new JLabel("Nhóm:  "), BorderLayout.WEST);
        cboCategoryFilter = new JComboBox<>(new String[]{"-- Tất cả --", "Thực phẩm", "Đồ uống", "Đồ điện gia dụng"});
        filterRow.add(cboCategoryFilter, BorderLayout.CENTER);
        leftTop.add(filterRow);

        leftPanel.add(leftTop, BorderLayout.NORTH);

        // Bảng sản phẩm
        String[] pCols = {"Mã SP", "Tên sản phẩm", "Giá bán", "Tồn kho"};
        modelAvailableProducts = new DefaultTableModel(pCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblAvailableProducts = UIUtils.createCustomTable(modelAvailableProducts);
        tblAvailableProducts.setFont(UIUtils.BODY_FONT);
        tblAvailableProducts.setRowHeight(26);
        tblAvailableProducts.getTableHeader().setFont(UIUtils.BOLD_FONT);
        tblAvailableProducts.getTableHeader().setReorderingAllowed(false);
        tblAvailableProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblAvailableProducts.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblAvailableProducts.getColumnModel().getColumn(1).setPreferredWidth(215); // Nới rộng cho tên món dài
        tblAvailableProducts.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblAvailableProducts.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblAvailableProducts.getColumnModel().getColumn(0).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblAvailableProducts.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblAvailableProducts.getColumnModel().getColumn(2).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblAvailableProducts.getColumnModel().getColumn(3).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));

        JScrollPane scrollProd = new JScrollPane(tblAvailableProducts);
        scrollProd.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 1));
        UIUtils.installSmartDeselection(tblAvailableProducts, scrollProd);
        leftPanel.add(scrollProd, BorderLayout.CENTER);

        // Thanh thao tác dưới bên trái
        JPanel leftBottom = new JPanel(new BorderLayout(5, 5));
        leftBottom.setBackground(Color.WHITE);
        leftBottom.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        JLabel lblHint = new JLabel("Mẹo: Nhấp đúp chuột vào SP để thêm nhanh vào giỏ", JLabel.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(new Color(110, 120, 130));
        leftBottom.add(lblHint, BorderLayout.NORTH);

        JPanel spinAndBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        spinAndBtn.setBackground(Color.WHITE);
        spinAndBtn.add(new JLabel("SL mua:"));
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spinQuantity.setPreferredSize(new Dimension(65, 30));
        spinAndBtn.add(spinQuantity);

        btnAddToCart = UIUtils.createStyledButton("Thêm vào giỏ", UIUtils.PRIMARY_COLOR, Color.WHITE);
        spinAndBtn.add(btnAddToCart);
        leftBottom.add(spinAndBtn, BorderLayout.SOUTH);

        leftPanel.add(leftBottom, BorderLayout.SOUTH);

        // =========================================================================
        // --- 2. CENTER PANEL: Giỏ hàng quầy thu ngân ---
        // =========================================================================
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "2. GIỎ HÀNG THU NGÂN", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));

        String[] cartCols = {"Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
        modelCart = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblCart = UIUtils.createCustomTable(modelCart);
        tblCart.setFont(UIUtils.BODY_FONT);
        tblCart.setRowHeight(28);
        tblCart.getTableHeader().setFont(UIUtils.BOLD_FONT);
        tblCart.getTableHeader().setReorderingAllowed(false);
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCart.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblCart.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblCart.getColumnModel().getColumn(3).setPreferredWidth(65);
        tblCart.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblCart.getColumnModel().getColumn(0).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblCart.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblCart.getColumnModel().getColumn(2).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblCart.getColumnModel().getColumn(3).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblCart.getColumnModel().getColumn(4).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));

        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 1));
        UIUtils.installSmartDeselection(tblCart, scrollCart);
        centerPanel.add(scrollCart, BorderLayout.CENTER);

        // Thanh công cụ giỏ hàng: Tách 2 hàng rõ ràng, triệt tiêu 100% lỗi lẹm chữ "it hàng"
        JPanel centerBottom = new JPanel(new BorderLayout(0, 6));
        centerBottom.setBackground(Color.WHITE);
        centerBottom.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        // Hàng trên: Thanh trạng thái tổng số lượng giỏ hàng (Rộng rãi 100% chiều ngang)
        JPanel summaryBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        summaryBar.setBackground(new Color(245, 247, 250));
        summaryBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 238), 1),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        lblCartSummary = new JLabel("Tổng giỏ hàng: 0 mặt hàng (0 sản phẩm)", JLabel.RIGHT);
        lblCartSummary.setFont(UIUtils.BOLD_FONT);
        lblCartSummary.setForeground(new Color(30, 41, 59));
        summaryBar.add(lblCartSummary);
        centerBottom.add(summaryBar, BorderLayout.NORTH);

        // Hàng dưới: 6 nút thao tác giỏ hàng
        JPanel cartActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        cartActions.setBackground(Color.WHITE);

        btnIncreaseQty = UIUtils.createStyledButton("+1 SL", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnDecreaseQty = UIUtils.createStyledButton("-1 SL", UIUtils.GRAY_COLOR, Color.WHITE);
        btnRemoveFromCart = UIUtils.createStyledButton("Xóa món", UIUtils.DANGER_COLOR, Color.WHITE);
        btnClearCart = UIUtils.createStyledButton("Làm trống", new Color(108, 117, 125), Color.WHITE);
        btnHoldBill = UIUtils.createStyledButton("Treo đơn", new Color(217, 119, 6), Color.WHITE);
        btnResumeBill = UIUtils.createStyledButton("Mở đơn treo (0)", new Color(13, 110, 253), Color.WHITE);

        cartActions.add(btnIncreaseQty);
        cartActions.add(btnDecreaseQty);
        cartActions.add(btnRemoveFromCart);
        cartActions.add(btnClearCart);
        cartActions.add(btnHoldBill);
        cartActions.add(btnResumeBill);

        centerBottom.add(cartActions, BorderLayout.SOUTH);
        centerPanel.add(centerBottom, BorderLayout.SOUTH);

        // =========================================================================
        // --- 3. RIGHT PANEL: Thông tin khách hàng & Thanh toán (Cards layout) ---
        // =========================================================================
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setPreferredSize(new Dimension(410, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "3. THANH TOÁN & HÓA ĐƠN", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));

        // Container xếp dọc các Card
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(Color.WHITE);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // -------------------------------------------------------------
        // CARD 1: KHÁCH HÀNG & THÀNH VIÊN
        // -------------------------------------------------------------
        JPanel cardCustomer = new JPanel();
        cardCustomer.setLayout(new BoxLayout(cardCustomer, BoxLayout.Y_AXIS));
        cardCustomer.setBackground(Color.WHITE);
        cardCustomer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
                        "Khách Hàng & Thành Viên", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), UIUtils.PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(6, 10, 8, 10)
        ));

        JPanel custRow1 = new JPanel(new BorderLayout(8, 0));
        custRow1.setBackground(Color.WHITE);
        custRow1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        custRow1.add(new JLabel("Chọn KH:"), BorderLayout.WEST);

        cboCustomer = new JComboBox<>();
        cboCustomer.setPreferredSize(new Dimension(170, 30));
        custRow1.add(cboCustomer, BorderLayout.CENTER);

        btnNewCustomer = UIUtils.createStyledButton("+ Thêm", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnNewCustomer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNewCustomer.setPreferredSize(new Dimension(80, 30));
        custRow1.add(btnNewCustomer, BorderLayout.EAST);
        cardCustomer.add(custRow1);

        cardCustomer.add(Box.createVerticalStrut(6));

        // Badge trạng thái khách hàng
        JPanel badgePanel = new JPanel(new BorderLayout(5, 0));
        badgePanel.setBackground(new Color(240, 246, 255));
        badgePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 225, 255), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        badgePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        lblCustomerType = new JLabel("Khách lẻ (Giảm 0%)");
        lblCustomerType.setFont(UIUtils.BOLD_FONT);
        lblCustomerType.setForeground(new Color(13, 110, 253));

        lblCustomerPoints = new JLabel("Điểm: 0", JLabel.RIGHT);
        lblCustomerPoints.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCustomerPoints.setForeground(new Color(80, 90, 100));

        badgePanel.add(lblCustomerType, BorderLayout.WEST);
        badgePanel.add(lblCustomerPoints, BorderLayout.EAST);
        cardCustomer.add(badgePanel);

        cardsContainer.add(cardCustomer);
        cardsContainer.add(Box.createVerticalStrut(8));

        // -------------------------------------------------------------
        // CARD 2: CHI TIẾT TIỀN HÀNG & CHIẾT KHẤU
        // -------------------------------------------------------------
        JPanel cardBilling = new JPanel();
        cardBilling.setLayout(new BoxLayout(cardBilling, BoxLayout.Y_AXIS));
        cardBilling.setBackground(Color.WHITE);
        cardBilling.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
                        "Chi Tiết Thanh Toán", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), UIUtils.PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(6, 10, 8, 10)
        ));

        lblTotalAmount = new JLabel("0 đ", JLabel.RIGHT);
        lblTotalAmount.setFont(UIUtils.BOLD_FONT);
        cardBilling.add(createRowPanel("Tổng tiền hàng:", lblTotalAmount, UIUtils.BODY_FONT, Color.BLACK));
        cardBilling.add(Box.createVerticalStrut(4));

        lblDiscountAmount = new JLabel("- 0 đ", JLabel.RIGHT);
        lblDiscountAmount.setFont(UIUtils.BOLD_FONT);
        lblDiscountAmount.setForeground(UIUtils.DANGER_COLOR);
        cardBilling.add(createRowPanel("Chiết khấu thành viên:", lblDiscountAmount, UIUtils.BODY_FONT, UIUtils.DANGER_COLOR));
        cardBilling.add(Box.createVerticalStrut(6));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        cardBilling.add(sep);
        cardBilling.add(Box.createVerticalStrut(6));

        lblFinalAmount = new JLabel("0 đ", JLabel.RIGHT);
        lblFinalAmount.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblFinalAmount.setForeground(UIUtils.SUCCESS_COLOR);
        JLabel lblPayTitle = new JLabel("CẦN THANH TOÁN:");
        lblPayTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardBilling.add(createRowPanel(lblPayTitle, lblFinalAmount));

        cardsContainer.add(cardBilling);
        cardsContainer.add(Box.createVerticalStrut(8));

        // -------------------------------------------------------------
        // CARD 3: TIỀN KHÁCH ĐƯA & THỐI LẠI (KÈM NÚT NHANH)
        // -------------------------------------------------------------
        JPanel cardCash = new JPanel();
        cardCash.setLayout(new BoxLayout(cardCash, BoxLayout.Y_AXIS));
        cardCash.setBackground(Color.WHITE);
        cardCash.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
                        "Thu Tiền & Tiền Thừa", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), UIUtils.PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(6, 10, 8, 10)
        ));

        // Dòng nhập tiền khách đưa
        JPanel cashRow = new JPanel(new BorderLayout(8, 0));
        cashRow.setBackground(Color.WHITE);
        cashRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        JLabel lblGivenTitle = new JLabel("Tiền khách đưa:");
        lblGivenTitle.setFont(UIUtils.BOLD_FONT);
        cashRow.add(lblGivenTitle, BorderLayout.WEST);

        txtCashGiven = new JTextField("0");
        txtCashGiven.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtCashGiven.setHorizontalAlignment(JTextField.RIGHT);
        txtCashGiven.setPreferredSize(new Dimension(170, 32));
        cashRow.add(txtCashGiven, BorderLayout.CENTER);
        cardCash.add(cashRow);

        cardCash.add(Box.createVerticalStrut(6));

        // Hàng nút chọn nhanh tiền mặt (Quick Cash Buttons)
        JPanel quickCashPanel = new JPanel(new GridLayout(1, 5, 4, 0));
        quickCashPanel.setBackground(Color.WHITE);
        quickCashPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        btnExactCash = createQuickCashBtn("Đủ tiền");
        btnCash50k = createQuickCashBtn("50k");
        btnCash100k = createQuickCashBtn("100k");
        btnCash200k = createQuickCashBtn("200k");
        btnCash500k = createQuickCashBtn("500k");

        quickCashPanel.add(btnExactCash);
        quickCashPanel.add(btnCash50k);
        quickCashPanel.add(btnCash100k);
        quickCashPanel.add(btnCash200k);
        quickCashPanel.add(btnCash500k);
        cardCash.add(quickCashPanel);

        cardCash.add(Box.createVerticalStrut(8));

        // Dòng tiền thối lại
        lblReturnCash = new JLabel("0 đ", JLabel.RIGHT);
        lblReturnCash.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblReturnCash.setForeground(new Color(13, 110, 253));
        JLabel lblReturnTitle = new JLabel("Tiền thừa trả khách:");
        lblReturnTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cardCash.add(createRowPanel(lblReturnTitle, lblReturnCash));

        cardsContainer.add(cardCash);
        cardsContainer.add(Box.createVerticalGlue());

        rightPanel.add(cardsContainer, BorderLayout.CENTER);

        // Nút hành động phía dưới
        JPanel bottomActions = new JPanel(new GridLayout(2, 1, 0, 6));
        bottomActions.setBackground(Color.WHITE);
        bottomActions.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));

        btnCheckout = UIUtils.createStyledButton("THANH TOÁN & IN HÓA ĐƠN", UIUtils.SUCCESS_COLOR, Color.WHITE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.setPreferredSize(new Dimension(0, 48));

        btnCancelOrder = UIUtils.createStyledButton("Làm mới quầy / Hủy đơn", UIUtils.GRAY_COLOR, Color.WHITE);
        btnCancelOrder.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelOrder.setPreferredSize(new Dimension(0, 32));

        bottomActions.add(btnCheckout);
        bottomActions.add(btnCancelOrder);
        rightPanel.add(bottomActions, BorderLayout.SOUTH);

        // Bố cục tổng 3 phần thích ứng hoàn hảo
        JPanel contentContainer = new JPanel(new BorderLayout(10, 0));
        contentContainer.setBackground(UIUtils.BG_LIGHT);
        contentContainer.add(leftPanel, BorderLayout.WEST);
        contentContainer.add(centerPanel, BorderLayout.CENTER);
        contentContainer.add(rightPanel, BorderLayout.EAST);

        add(contentContainer, BorderLayout.CENTER);
    }

    private JPanel createRowPanel(String title, JComponent valueComp, Font titleFont, Color titleColor) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(titleFont);
        if (titleColor != null) lbl.setForeground(titleColor);
        return createRowPanel(lbl, valueComp);
    }

    private JPanel createRowPanel(JComponent leftComp, JComponent rightComp) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.add(leftComp, BorderLayout.WEST);
        row.add(rightComp, BorderLayout.EAST);
        return row;
    }

    private JButton createQuickCashBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(new Color(240, 243, 248));
        btn.setForeground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(205, 215, 225), 1));
        btn.setMargin(new Insets(2, 2, 2, 2));
        return btn;
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
        int totalItems = 0;
        int totalQuantity = 0;

        for (InvoiceItem item : items) {
            totalItems++;
            totalQuantity += item.getQuantity();
            modelCart.addRow(new Object[]{
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    UIUtils.formatCurrency(item.getUnitPrice()),
                    item.getQuantity(),
                    UIUtils.formatCurrency(item.getSubTotal())
            });
        }

        lblCartSummary.setText(String.format("Tổng giỏ hàng: %d mặt hàng (%d sản phẩm)", totalItems, totalQuantity));
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
    public JComboBox<String> getCboCategoryFilter() { return cboCategoryFilter; }
    public JSpinner getSpinQuantity() { return spinQuantity; }
    public JButton getBtnAddToCart() { return btnAddToCart; }
    public JTable getTblCart() { return tblCart; }
    public JButton getBtnIncreaseQty() { return btnIncreaseQty; }
    public JButton getBtnDecreaseQty() { return btnDecreaseQty; }
    public JButton getBtnRemoveFromCart() { return btnRemoveFromCart; }
    public JButton getBtnClearCart() { return btnClearCart; }
    public JButton getBtnHoldBill() { return btnHoldBill; }
    public JButton getBtnResumeBill() { return btnResumeBill; }
    public JLabel getLblCartSummary() { return lblCartSummary; }
    public JComboBox<Customer> getCboCustomer() { return cboCustomer; }
    public JButton getBtnNewCustomer() { return btnNewCustomer; }
    public JLabel getLblCustomerType() { return lblCustomerType; }
    public JLabel getLblCustomerPoints() { return lblCustomerPoints; }
    public JLabel getLblTotalAmount() { return lblTotalAmount; }
    public JLabel getLblDiscountAmount() { return lblDiscountAmount; }
    public JLabel getLblFinalAmount() { return lblFinalAmount; }
    public JTextField getTxtCashGiven() { return txtCashGiven; }
    public JLabel getLblReturnCash() { return lblReturnCash; }
    public JButton getBtnCheckout() { return btnCheckout; }
    public JButton getBtnCancelOrder() { return btnCancelOrder; }

    public JButton getBtnExactCash() { return btnExactCash; }
    public JButton getBtnCash50k() { return btnCash50k; }
    public JButton getBtnCash100k() { return btnCash100k; }
    public JButton getBtnCash200k() { return btnCash200k; }
    public JButton getBtnCash500k() { return btnCash500k; }
}
