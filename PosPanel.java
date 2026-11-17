import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Quầy Thu Ngân & Bán hàng (PosPanel) chuẩn phong cách Enterprise POS:
 * - Chia 3 cột trực quan (Danh mục SP -> Giỏ hàng -> Thanh toán).
 * - Bảng hiển thị chuẩn SaaS, chiều cao dòng 32px, xen kẽ màu nền.
 * - Thẻ thanh toán tài chính nổi bật: Số tiền cần thanh toán hiển thị siêu to, màu xanh Emerald uy tín.
 * - Nút chọn nhanh tiền mặt (Quick Cash) và phím tắt thao tác nhanh cho thu ngân.
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
        setLayout(new BorderLayout(0, 10));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        initComponents();
    }

    private void initComponents() {
        // =========================================================================
        // --- 1. LEFT PANEL: Danh sách sản phẩm có thể bán (Card Panel) ---
        // =========================================================================
        JPanel leftPanel = UIUtils.createCardPanel(new BorderLayout(0, 8));
        leftPanel.setPreferredSize(new Dimension(450, 0));

        // Tiêu đề khối
        leftPanel.add(UIUtils.createSectionHeader("1. CHỌN SẢN PHẨM BÁN", "Nhấp đúp vào sản phẩm để thêm nhanh vào giỏ", UIUtils.VectorIcons.box(18, UIUtils.PRIMARY_COLOR)), BorderLayout.NORTH);

        // Thanh tìm kiếm và lọc danh mục
        JPanel leftTop = new JPanel(new GridLayout(2, 1, 4, 6));
        leftTop.setOpaque(false);
        leftTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm SP:");
        lblSearch.setFont(UIUtils.SMALL_BOLD);
        lblSearch.setForeground(UIUtils.NAVY_SLATE);
        searchRow.add(lblSearch, BorderLayout.WEST);

        txtSearchProduct = new JTextField();
        UIUtils.styleTextField(txtSearchProduct);
        txtSearchProduct.setToolTipText("Gõ mã hoặc tên sản phẩm...");
        searchRow.add(txtSearchProduct, BorderLayout.CENTER);
        leftTop.add(searchRow);

        JPanel filterRow = new JPanel(new BorderLayout(8, 0));
        filterRow.setOpaque(false);
        JLabel lblCat = new JLabel("Nhóm:  ");
        lblCat.setFont(UIUtils.SMALL_BOLD);
        lblCat.setForeground(UIUtils.NAVY_SLATE);
        filterRow.add(lblCat, BorderLayout.WEST);

        cboCategoryFilter = new JComboBox<>(new String[]{"-- Tất cả --", "Thực phẩm", "Đồ uống", "Đồ điện gia dụng"});
        UIUtils.styleComboBox(cboCategoryFilter);
        filterRow.add(cboCategoryFilter, BorderLayout.CENTER);
        leftTop.add(filterRow);

        JPanel leftCenter = new JPanel(new BorderLayout(0, 6));
        leftCenter.setOpaque(false);
        leftCenter.add(leftTop, BorderLayout.NORTH);

        // Bảng sản phẩm
        String[] pCols = {"Mã SP", "Tên sản phẩm", "Giá bán", "Tồn kho"};
        modelAvailableProducts = new DefaultTableModel(pCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblAvailableProducts = UIUtils.createCustomTable(modelAvailableProducts);
        tblAvailableProducts.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblAvailableProducts.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblAvailableProducts.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblAvailableProducts.getColumnModel().getColumn(3).setPreferredWidth(65);

        tblAvailableProducts.getColumnModel().getColumn(0).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.CENTER));
        tblAvailableProducts.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblAvailableProducts.getColumnModel().getColumn(2).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblAvailableProducts.getColumnModel().getColumn(3).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));

        JScrollPane scrollProd = new JScrollPane(tblAvailableProducts);
        scrollProd.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        UIUtils.installSmartDeselection(tblAvailableProducts, scrollProd);
        leftCenter.add(scrollProd, BorderLayout.CENTER);

        leftPanel.add(leftCenter, BorderLayout.CENTER);

        // Thanh thao tác dưới bên trái (Số lượng & Nút thêm)
        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        leftBottom.setOpaque(false);

        JLabel lblQty = new JLabel("Số lượng mua:");
        lblQty.setFont(UIUtils.SMALL_BOLD);
        lblQty.setForeground(UIUtils.NAVY_SLATE);
        leftBottom.add(lblQty);

        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spinQuantity.setFont(UIUtils.BOLD_FONT);
        spinQuantity.setPreferredSize(new Dimension(70, 32));
        leftBottom.add(spinQuantity);

        btnAddToCart = UIUtils.createStyledButton("Thêm vào giỏ", UIUtils.VectorIcons.cart(15, Color.WHITE), UIUtils.PRIMARY_COLOR, Color.WHITE);
        leftBottom.add(btnAddToCart);

        leftPanel.add(leftBottom, BorderLayout.SOUTH);

        // =========================================================================
        // --- 2. CENTER PANEL: Giỏ hàng quầy thu ngân (Card Panel) ---
        // =========================================================================
        JPanel centerPanel = UIUtils.createCardPanel(new BorderLayout(0, 8));

        // Tiêu đề khối
        centerPanel.add(UIUtils.createSectionHeader("2. GIỎ HÀNG THU NGÂN", "Quản lý danh sách các món khách đã chọn mua", UIUtils.VectorIcons.cart(18, UIUtils.SUCCESS_COLOR)), BorderLayout.NORTH);

        String[] cartCols = {"Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
        modelCart = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblCart = UIUtils.createCustomTable(modelCart);
        tblCart.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblCart.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblCart.getColumnModel().getColumn(3).setPreferredWidth(65);
        tblCart.getColumnModel().getColumn(4).setPreferredWidth(100);

        tblCart.getColumnModel().getColumn(0).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.CENTER));
        tblCart.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblCart.getColumnModel().getColumn(2).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblCart.getColumnModel().getColumn(3).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblCart.getColumnModel().getColumn(4).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));

        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        UIUtils.installSmartDeselection(tblCart, scrollCart);
        centerPanel.add(scrollCart, BorderLayout.CENTER);

        // Thanh công cụ giỏ hàng phía dưới
        JPanel centerBottom = new JPanel(new BorderLayout(0, 8));
        centerBottom.setOpaque(false);

        // Thanh trạng thái tổng số lượng giỏ hàng
        JPanel summaryBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        summaryBar.setBackground(new Color(248, 250, 252));
        summaryBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        lblCartSummary = new JLabel("Tổng giỏ hàng: 0 mặt hàng (0 sản phẩm)", JLabel.RIGHT);
        lblCartSummary.setFont(UIUtils.BOLD_FONT);
        lblCartSummary.setForeground(UIUtils.NAVY_SLATE);
        summaryBar.add(lblCartSummary);
        centerBottom.add(summaryBar, BorderLayout.NORTH);

        // Hàng nút thao tác giỏ hàng
        JPanel cartActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        cartActions.setOpaque(false);

        btnIncreaseQty = UIUtils.createStyledButton("+1 SL", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnDecreaseQty = UIUtils.createStyledButton("-1 SL", UIUtils.GRAY_COLOR, Color.WHITE);
        btnRemoveFromCart = UIUtils.createStyledButton("Xóa món", UIUtils.DANGER_COLOR, Color.WHITE);
        btnClearCart = UIUtils.createStyledButton("Làm trống", new Color(108, 117, 125), Color.WHITE);
        btnHoldBill = UIUtils.createStyledButton("Treo đơn", UIUtils.WARNING_COLOR, Color.WHITE);
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
        // --- 3. RIGHT PANEL: Thông tin khách hàng & Thanh toán (Card Panel) ---
        // =========================================================================
        JPanel rightPanel = UIUtils.createCardPanel(new BorderLayout(0, 10));
        rightPanel.setPreferredSize(new Dimension(410, 0));

        // Tiêu đề khối
        rightPanel.add(UIUtils.createSectionHeader("3. THANH TOÁN & HÓA ĐƠN", "Tính tiền, áp dụng chiết khấu và in hóa đơn", UIUtils.VectorIcons.receipt(18, UIUtils.WARNING_COLOR)), BorderLayout.NORTH);

        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        // --- KHỐI KHÁCH HÀNG & THÀNH VIÊN ---
        JPanel cardCustomer = new JPanel();
        cardCustomer.setLayout(new BoxLayout(cardCustomer, BoxLayout.Y_AXIS));
        cardCustomer.setBackground(new Color(248, 250, 252));
        cardCustomer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JPanel custRow1 = new JPanel(new BorderLayout(8, 0));
        custRow1.setOpaque(false);
        custRow1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel lblCust = new JLabel("Khách hàng:");
        lblCust.setFont(UIUtils.SMALL_BOLD);
        lblCust.setForeground(UIUtils.NAVY_SLATE);
        custRow1.add(lblCust, BorderLayout.WEST);

        cboCustomer = new JComboBox<>();
        UIUtils.styleComboBox(cboCustomer);
        cboCustomer.setPreferredSize(new Dimension(170, 30));
        custRow1.add(cboCustomer, BorderLayout.CENTER);

        btnNewCustomer = UIUtils.createStyledButton("+ Thêm", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnNewCustomer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNewCustomer.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btnNewCustomer.setPreferredSize(new Dimension(92, 32));
        custRow1.add(btnNewCustomer, BorderLayout.EAST);
        cardCustomer.add(custRow1);
        cardCustomer.add(Box.createVerticalStrut(6));

        // Badge trạng thái khách hàng
        JPanel badgePanel = new JPanel(new BorderLayout(5, 0));
        badgePanel.setBackground(new Color(239, 246, 255));
        badgePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        badgePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        lblCustomerType = new JLabel("Khách lẻ (Chiết khấu 0%)");
        lblCustomerType.setFont(UIUtils.BOLD_FONT);
        lblCustomerType.setForeground(UIUtils.PRIMARY_COLOR);

        lblCustomerPoints = new JLabel("Điểm: 0", JLabel.RIGHT);
        lblCustomerPoints.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCustomerPoints.setForeground(UIUtils.SLATE_TEXT);

        badgePanel.add(lblCustomerType, BorderLayout.WEST);
        badgePanel.add(lblCustomerPoints, BorderLayout.EAST);
        cardCustomer.add(badgePanel);

        cardsContainer.add(cardCustomer);
        cardsContainer.add(Box.createVerticalStrut(10));

        // --- KHỐI CHI TIẾT THANH TOÁN (BILLING) ---
        JPanel cardBilling = new JPanel();
        cardBilling.setLayout(new BoxLayout(cardBilling, BoxLayout.Y_AXIS));
        cardBilling.setBackground(new Color(248, 250, 252));
        cardBilling.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        lblTotalAmount = new JLabel("0 đ", JLabel.RIGHT);
        lblTotalAmount.setFont(UIUtils.BOLD_FONT);
        lblTotalAmount.setForeground(UIUtils.NAVY_SLATE);
        cardBilling.add(createRowPanel("Tổng tiền hàng:", lblTotalAmount, UIUtils.BODY_FONT, UIUtils.SLATE_TEXT));
        cardBilling.add(Box.createVerticalStrut(4));

        lblDiscountAmount = new JLabel("- 0 đ", JLabel.RIGHT);
        lblDiscountAmount.setFont(UIUtils.BOLD_FONT);
        lblDiscountAmount.setForeground(UIUtils.DANGER_COLOR);
        cardBilling.add(createRowPanel("Chiết khấu thành viên:", lblDiscountAmount, UIUtils.BODY_FONT, UIUtils.DANGER_COLOR));
        cardBilling.add(Box.createVerticalStrut(8));

        JSeparator sep = new JSeparator();
        sep.setForeground(UIUtils.BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        cardBilling.add(sep);
        cardBilling.add(Box.createVerticalStrut(8));

        lblFinalAmount = new JLabel("0 đ", JLabel.RIGHT);
        lblFinalAmount.setFont(UIUtils.HUGE_PRICE_FONT);
        lblFinalAmount.setForeground(UIUtils.SUCCESS_COLOR);

        JLabel lblPayTitle = new JLabel("CẦN THANH TOÁN:");
        lblPayTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPayTitle.setForeground(UIUtils.NAVY_DARK);
        cardBilling.add(createRowPanel(lblPayTitle, lblFinalAmount));

        cardsContainer.add(cardBilling);
        cardsContainer.add(Box.createVerticalStrut(10));

        // --- KHỐI TIỀN KHÁCH ĐƯA & TIỀN THỐI LẠI ---
        JPanel cardCash = new JPanel();
        cardCash.setLayout(new BoxLayout(cardCash, BoxLayout.Y_AXIS));
        cardCash.setBackground(new Color(248, 250, 252));
        cardCash.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Dòng nhập tiền khách đưa
        JPanel cashRow = new JPanel(new BorderLayout(8, 0));
        cashRow.setOpaque(false);
        cashRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        JLabel lblGivenTitle = new JLabel("Tiền khách đưa:");
        lblGivenTitle.setFont(UIUtils.BOLD_FONT);
        lblGivenTitle.setForeground(UIUtils.NAVY_SLATE);
        cashRow.add(lblGivenTitle, BorderLayout.WEST);

        txtCashGiven = new JTextField("0");
        txtCashGiven.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtCashGiven.setHorizontalAlignment(JTextField.RIGHT);
        UIUtils.styleTextField(txtCashGiven);
        txtCashGiven.setPreferredSize(new Dimension(170, 32));
        cashRow.add(txtCashGiven, BorderLayout.CENTER);
        cardCash.add(cashRow);
        cardCash.add(Box.createVerticalStrut(6));

        // Hàng nút chọn nhanh tiền mặt (Quick Cash Buttons)
        JPanel quickCashPanel = new JPanel(new GridLayout(1, 5, 4, 0));
        quickCashPanel.setOpaque(false);
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
        lblReturnCash.setForeground(UIUtils.PRIMARY_COLOR);
        JLabel lblReturnTitle = new JLabel("Tiền thừa trả khách:");
        lblReturnTitle.setFont(UIUtils.BOLD_FONT);
        lblReturnTitle.setForeground(UIUtils.NAVY_SLATE);
        cardCash.add(createRowPanel(lblReturnTitle, lblReturnCash));

        cardsContainer.add(cardCash);
        cardsContainer.add(Box.createVerticalGlue());

        rightPanel.add(cardsContainer, BorderLayout.CENTER);

        // Nút hành động phía dưới
        JPanel bottomActions = new JPanel(new GridLayout(2, 1, 0, 8));
        bottomActions.setOpaque(false);
        bottomActions.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));

        btnCheckout = UIUtils.createStyledButton("THANH TOÁN & IN HÓA ĐƠN", UIUtils.VectorIcons.receipt(18, Color.WHITE), UIUtils.SUCCESS_COLOR, Color.WHITE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.setPreferredSize(new Dimension(0, 46));

        btnCancelOrder = UIUtils.createStyledButton("Làm mới quầy / Hủy đơn", UIUtils.VectorIcons.refresh(14, Color.WHITE), UIUtils.GRAY_COLOR, Color.WHITE);
        btnCancelOrder.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelOrder.setPreferredSize(new Dimension(0, 32));

        bottomActions.add(btnCheckout);
        bottomActions.add(btnCancelOrder);
        rightPanel.add(bottomActions, BorderLayout.SOUTH);

        // Bố cục tổng 3 phần
        JPanel contentContainer = new JPanel(new BorderLayout(12, 0));
        contentContainer.setOpaque(false);
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
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.add(leftComp, BorderLayout.WEST);
        row.add(rightComp, BorderLayout.EAST);
        return row;
    }

    private JButton createQuickCashBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(Color.WHITE);
        btn.setForeground(UIUtils.NAVY_SLATE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_DARK, 1));
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
