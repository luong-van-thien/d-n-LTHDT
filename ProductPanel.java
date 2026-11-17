import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Giao diện Quản lý Kho & Sản phẩm (ProductPanel) chuẩn phong cách Enterprise SaaS:
 * - Thanh công cụ lọc & tìm kiếm thông minh với icon sắc nét.
 * - Bảng danh mục sản phẩm hiện đại (Huy hiệu Badge Pills phân loại danh mục & cảnh báo).
 * - Khung nhập liệu bo góc với hiệu ứng viền phát sáng (Focus Glow).
 * - Khóa quyền tự động khi tài khoản ở vai trò Thu ngân (Cashier Mode).
 */
public class ProductPanel extends JPanel {
    // Table Components
    private JTable tblProducts;
    private DefaultTableModel tableModel;

    // Filter & Search Components
    private JTextField txtSearch;
    private JComboBox<String> cboFilterCategory;
    private JComboBox<String> cboSort;
    private JButton btnSearch;
    private JButton btnResetFilter;
    private JButton btnAlertFilter;
    private JButton btnCreateRestockOrder;
    private JButton btnExportCsv;

    // Form Input Components
    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<Category> cboCategory;
    private JTextField txtImportPrice;
    private JTextField txtSellingPrice;
    private JTextField txtQuantity;

    // Extra fields
    private JPanel extraCardPanel;
    private CardLayout extraCardLayout;

    // Food fields
    private JDateChooser dateFoodNsx;
    private JDateChooser dateFoodHsd;

    // Beverage fields
    private JTextField txtBevVolume;
    private JCheckBox chkBevAlcohol;
    private JDateChooser dateBevHsd;

    // Electronic fields
    private JTextField txtElecWarranty;
    private JTextField txtElecWatt;

    // Action Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private JPanel bottomPanel;
    private JLabel lblFormTitle;

    public ProductPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        initComponents();
    }

    private void initComponents() {
        // =========================================================================
        // 1. TOP PANEL: THANH TÌM KIẾM, LỌC & THAO TÁC (2 HÀNG RÕ RÀNG)
        // =========================================================================
        JPanel topPanel = UIUtils.createCardPanel(new BorderLayout(0, 8));

        // Hàng 1: Tìm kiếm, Bộ lọc phân loại & Sắp xếp
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        filterRow.setOpaque(false);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(UIUtils.BOLD_FONT);
        lblSearch.setForeground(UIUtils.NAVY_SLATE);
        filterRow.add(lblSearch);

        txtSearch = new JTextField(14);
        UIUtils.styleTextField(txtSearch);
        txtSearch.setToolTipText("Nhập mã hoặc tên sản phẩm để tìm nhanh...");
        filterRow.add(txtSearch);

        JLabel lblCat = new JLabel("Loại SP:");
        lblCat.setFont(UIUtils.BOLD_FONT);
        lblCat.setForeground(UIUtils.NAVY_SLATE);
        filterRow.add(lblCat);

        cboFilterCategory = new JComboBox<>(new String[]{"-- Tất cả --", "Thực phẩm", "Đồ uống", "Đồ điện gia dụng"});
        UIUtils.styleComboBox(cboFilterCategory);
        filterRow.add(cboFilterCategory);

        JLabel lblSort = new JLabel("Sắp xếp:");
        lblSort.setFont(UIUtils.BOLD_FONT);
        lblSort.setForeground(UIUtils.NAVY_SLATE);
        filterRow.add(lblSort);

        cboSort = new JComboBox<>(new String[]{
                "Mặc định (Tên A-Z)",
                "Tên Z-A",
                "Giá bán tăng dần",
                "Giá bán giảm dần",
                "Tồn kho giảm dần",
                "Lợi nhuận cao nhất"
        });
        UIUtils.styleComboBox(cboSort);
        filterRow.add(cboSort);

        btnSearch = UIUtils.createStyledButton("Lọc / Tìm", UIUtils.VectorIcons.search(15, Color.WHITE), UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnResetFilter = UIUtils.createStyledButton("Tất cả sản phẩm", UIUtils.VectorIcons.refresh(15, Color.WHITE), new Color(59, 130, 246), Color.WHITE);
        btnResetFilter.setToolTipText("Bấm để hiển thị đầy đủ toàn bộ kho hàng");
        filterRow.add(btnSearch);
        filterRow.add(btnResetFilter);

        // Hàng 2: Các nút hành động nghiệp vụ kho & xuất báo cáo
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        actionRow.setOpaque(false);

        JLabel lblAction = new JLabel("Nghiệp vụ kho:");
        lblAction.setFont(UIUtils.BOLD_FONT);
        lblAction.setForeground(UIUtils.NAVY_SLATE);
        actionRow.add(lblAction);

        btnAlertFilter = UIUtils.createStyledButton("Cảnh báo tồn/hạn", UIUtils.VectorIcons.alert(15, Color.WHITE), UIUtils.WARNING_COLOR, Color.WHITE);
        btnAlertFilter.setToolTipText("Bật/Tắt chế độ xem các sản phẩm tồn kho ít (<= 5) hoặc sắp hết hạn");

        btnCreateRestockOrder = UIUtils.createStyledButton("Tạo Phiếu Nhập Hàng", UIUtils.VectorIcons.box(15, Color.WHITE), new Color(194, 65, 12), Color.WHITE);
        btnExportCsv = UIUtils.createStyledButton("Xuất file báo cáo", UIUtils.VectorIcons.receipt(15, Color.WHITE), UIUtils.SUCCESS_COLOR, Color.WHITE);

        actionRow.add(btnAlertFilter);
        actionRow.add(btnCreateRestockOrder);
        actionRow.add(btnExportCsv);

        topPanel.add(filterRow, BorderLayout.NORTH);
        topPanel.add(actionRow, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // =========================================================================
        // 2. CENTER PANEL: BẢNG SẢN PHẨM HIỆN ĐẠI (SAAS TABLE)
        // =========================================================================
        String[] columns = {"Mã SP", "Tên sản phẩm", "Phân loại", "Giá nhập", "Giá bán", "Tồn kho", "Lợi nhuận/SP", "Thông tin chi tiết / Cảnh báo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblProducts = UIUtils.createCustomTable(tableModel);
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(210);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblProducts.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblProducts.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblProducts.getColumnModel().getColumn(5).setPreferredWidth(65);
        tblProducts.getColumnModel().getColumn(6).setPreferredWidth(95);
        tblProducts.getColumnModel().getColumn(7).setPreferredWidth(240);

        // Cài đặt Huy hiệu (Badge Pill) cho cột Phân loại
        tblProducts.getColumnModel().getColumn(2).setCellRenderer(new UIUtils.BadgeCellRenderer());

        // Renderer thông minh cho các cột còn lại: căn lề, cảnh báo tồn ít & tooltip
        tblProducts.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (value != null && !value.toString().trim().isEmpty()) {
                    setToolTipText("<html><body style='padding: 4px; font-family: Segoe UI; font-size: 11px;'>" + value.toString() + "</body></html>");
                } else {
                    setToolTipText(null);
                }

                if (!isSelected) {
                    try {
                        Object qtyObj = table.getValueAt(row, 5);
                        int qty = (qtyObj instanceof Integer) ? (Integer) qtyObj : Integer.parseInt(qtyObj.toString().trim());
                        String warningInfo = table.getValueAt(row, 7) != null ? table.getValueAt(row, 7).toString() : "";

                        if (qty == 0 || warningInfo.contains("[ĐÃ HẾT HẠN!")) {
                            c.setBackground(new Color(254, 242, 242)); // Đỏ nhạt
                            c.setForeground(new Color(185, 28, 28));
                        } else if (qty <= 5 || warningInfo.contains("[CẢNH BÁO")) {
                            c.setBackground(new Color(255, 251, 235)); // Vàng hổ phách nhạt
                            c.setForeground(new Color(180, 83, 9));
                        } else {
                            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                            c.setForeground(UIUtils.NAVY_SLATE);
                        }
                    } catch (Exception ex) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(UIUtils.NAVY_SLATE);
                    }
                }

                // Căn lề số tiền và số lượng về bên phải
                if (col >= 3 && col <= 6) {
                    setHorizontalAlignment(JLabel.RIGHT);
                    if (col == 4 || col == 6) {
                        setFont(UIUtils.BOLD_FONT);
                    } else {
                        setFont(UIUtils.BODY_FONT);
                    }
                } else if (col == 0) {
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(UIUtils.BOLD_FONT);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                    setFont(UIUtils.BODY_FONT);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblProducts);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        UIUtils.installSmartDeselection(tblProducts, scrollPane);

        add(scrollPane, BorderLayout.CENTER);

        // =========================================================================
        // 3. BOTTOM PANEL: FORM NHẬP LIỆU & THAO TÁC (CARD CONTAINER)
        // =========================================================================
        bottomPanel = UIUtils.createCardPanel(new BorderLayout(10, 8));

        // Tiêu đề khối
        lblFormTitle = new JLabel("THÔNG TIN CHI TIẾT SẢN PHẨM");
        lblFormTitle.setFont(UIUtils.HEADER_FONT);
        lblFormTitle.setForeground(UIUtils.NAVY_SLATE);
        lblFormTitle.setIcon(UIUtils.VectorIcons.box(18, UIUtils.PRIMARY_COLOR));
        lblFormTitle.setIconTextGap(8);
        bottomPanel.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridLayout(3, 4, 12, 8));
        formGrid.setOpaque(false);
        formGrid.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        txtId = new JTextField();
        UIUtils.styleTextField(txtId);
        txtName = new JTextField();
        UIUtils.styleTextField(txtName);
        cboCategory = new JComboBox<>(new Category[]{Category.FOOD, Category.BEVERAGE, Category.ELECTRONIC});
        UIUtils.styleComboBox(cboCategory);
        txtImportPrice = new JTextField();
        UIUtils.styleTextField(txtImportPrice);
        txtSellingPrice = new JTextField();
        UIUtils.styleTextField(txtSellingPrice);
        txtQuantity = new JTextField();
        UIUtils.styleTextField(txtQuantity);

        formGrid.add(createFieldLabel("Mã sản phẩm (*):"));
        formGrid.add(txtId);
        formGrid.add(createFieldLabel("Tên sản phẩm (*):"));
        formGrid.add(txtName);

        formGrid.add(createFieldLabel("Loại sản phẩm:"));
        formGrid.add(cboCategory);
        formGrid.add(createFieldLabel("Số lượng tồn kho (*):"));
        formGrid.add(txtQuantity);

        formGrid.add(createFieldLabel("Giá nhập (VNĐ) (*):"));
        formGrid.add(txtImportPrice);
        formGrid.add(createFieldLabel("Giá bán (VNĐ) (*):"));
        formGrid.add(txtSellingPrice);

        // Extra dynamic Card Panel
        extraCardLayout = new CardLayout();
        extraCardPanel = new JPanel(extraCardLayout);
        extraCardPanel.setOpaque(false);

        // Card Food
        JPanel foodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        foodPanel.setOpaque(false);
        foodPanel.add(createFieldLabel("Ngày SX:"));
        dateFoodNsx = new JDateChooser(LocalDate.now().minusMonths(1));
        foodPanel.add(dateFoodNsx);
        foodPanel.add(createFieldLabel("Hạn SD:"));
        dateFoodHsd = new JDateChooser(LocalDate.now().plusMonths(6));
        foodPanel.add(dateFoodHsd);

        // Card Beverage
        JPanel bevPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bevPanel.setOpaque(false);
        bevPanel.add(createFieldLabel("Dung tích (ml):"));
        txtBevVolume = new JTextField("330", 5);
        UIUtils.styleTextField(txtBevVolume);
        bevPanel.add(txtBevVolume);
        chkBevAlcohol = new JCheckBox("Có chứa cồn (bia/rượu)");
        chkBevAlcohol.setOpaque(false);
        chkBevAlcohol.setFont(UIUtils.BODY_FONT);
        bevPanel.add(chkBevAlcohol);
        bevPanel.add(createFieldLabel("HSD:"));
        dateBevHsd = new JDateChooser(LocalDate.now().plusMonths(9));
        bevPanel.add(dateBevHsd);

        // Card Electronic
        JPanel elecPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        elecPanel.setOpaque(false);
        elecPanel.add(createFieldLabel("Bảo hành (tháng):"));
        txtElecWarranty = new JTextField("12", 5);
        UIUtils.styleTextField(txtElecWarranty);
        elecPanel.add(txtElecWarranty);
        elecPanel.add(createFieldLabel("Công suất (Watt):"));
        txtElecWatt = new JTextField("1000", 6);
        UIUtils.styleTextField(txtElecWatt);
        elecPanel.add(txtElecWatt);

        extraCardPanel.add(foodPanel, Category.FOOD.name());
        extraCardPanel.add(bevPanel, Category.BEVERAGE.name());
        extraCardPanel.add(elecPanel, Category.ELECTRONIC.name());

        cboCategory.addActionListener(e -> {
            Category selected = (Category) cboCategory.getSelectedItem();
            if (selected != null) {
                extraCardLayout.show(extraCardPanel, selected.name());
            }
        });

        // Hàng nút thao tác (Action Buttons)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        buttonPanel.setOpaque(false);

        btnAdd = UIUtils.createStyledButton("Thêm mới", UIUtils.VectorIcons.box(15, Color.WHITE), UIUtils.SUCCESS_COLOR, Color.WHITE);
        btnUpdate = UIUtils.createStyledButton("Cập nhật", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnDelete = UIUtils.createStyledButton("Xóa sản phẩm", UIUtils.DANGER_COLOR, Color.WHITE);
        btnClear = UIUtils.createStyledButton("Làm mới biểu mẫu", UIUtils.VectorIcons.refresh(15, Color.WHITE), UIUtils.GRAY_COLOR, Color.WHITE);
        btnClear.setToolTipText("Làm mới biểu mẫu và tự động điền Mã sản phẩm mới tiếp theo để thêm hàng");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        JPanel bottomContainer = new JPanel(new BorderLayout(0, 4));
        bottomContainer.setOpaque(false);
        bottomContainer.add(formGrid, BorderLayout.NORTH);
        bottomContainer.add(extraCardPanel, BorderLayout.CENTER);
        bottomContainer.add(buttonPanel, BorderLayout.SOUTH);

        bottomPanel.add(bottomContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIUtils.SMALL_BOLD);
        lbl.setForeground(UIUtils.NAVY_SLATE);
        return lbl;
    }

    public void updateTable(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory().getDisplayName(),
                    UIUtils.formatCurrency(p.getImportPrice()),
                    UIUtils.formatCurrency(p.getSellingPrice()),
                    p.getQuantity(),
                    UIUtils.formatCurrency(p.calculateProfit()),
                    p.getSpecificInfo()
            });
        }
    }

    // Getters for Controller
    public JTable getTblProducts() { return tblProducts; }
    public JTextField getTxtSearch() { return txtSearch; }
    public JComboBox<String> getCboFilterCategory() { return cboFilterCategory; }
    public JComboBox<String> getCboSort() { return cboSort; }
    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnResetFilter() { return btnResetFilter; }
    public JButton getBtnAlertFilter() { return btnAlertFilter; }
    public JButton getBtnCreateRestockOrder() { return btnCreateRestockOrder; }
    public JButton getBtnExportCsv() { return btnExportCsv; }

    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtName() { return txtName; }
    public JComboBox<Category> getCboCategory() { return cboCategory; }
    public JTextField getTxtImportPrice() { return txtImportPrice; }
    public JTextField getTxtSellingPrice() { return txtSellingPrice; }
    public JTextField getTxtQuantity() { return txtQuantity; }

    public JDateChooser getDateFoodNsx() { return dateFoodNsx; }
    public JDateChooser getDateFoodHsd() { return dateFoodHsd; }
    public JTextField getTxtBevVolume() { return txtBevVolume; }
    public JCheckBox getChkBevAlcohol() { return chkBevAlcohol; }
    public JDateChooser getDateBevHsd() { return dateBevHsd; }
    public JTextField getTxtElecWarranty() { return txtElecWarranty; }
    public JTextField getTxtElecWatt() { return txtElecWatt; }

    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear() { return btnClear; }
    public CardLayout getExtraCardLayout() { return extraCardLayout; }
    public JPanel getExtraCardPanel() { return extraCardPanel; }

    /**
     * Bật/Tắt chế độ Thu ngân (Cashier Mode).
     */
    public void setCashierMode(boolean isCashier) {
        btnAdd.setEnabled(!isCashier);
        btnUpdate.setEnabled(!isCashier);
        btnDelete.setEnabled(!isCashier);
        btnClear.setEnabled(!isCashier);
        btnCreateRestockOrder.setEnabled(!isCashier);
        btnExportCsv.setEnabled(!isCashier);

        txtId.setEditable(!isCashier);
        txtName.setEditable(!isCashier);
        txtImportPrice.setEditable(!isCashier);
        txtSellingPrice.setEditable(!isCashier);
        txtQuantity.setEditable(!isCashier);
        cboCategory.setEnabled(!isCashier);

        dateFoodNsx.setEnabled(!isCashier);
        dateFoodHsd.setEnabled(!isCashier);
        txtBevVolume.setEditable(!isCashier);
        chkBevAlcohol.setEnabled(!isCashier);
        dateBevHsd.setEnabled(!isCashier);
        txtElecWarranty.setEditable(!isCashier);
        txtElecWatt.setEditable(!isCashier);

        if (lblFormTitle != null) {
            lblFormTitle.setText(isCashier
                    ? "THÔNG TIN CHI TIẾT SẢN PHẨM [CHẾ ĐỘ THU NGÂN - CHỈ ĐỌC]"
                    : "THÔNG TIN CHI TIẾT SẢN PHẨM");
            lblFormTitle.setForeground(isCashier ? UIUtils.WARNING_COLOR : UIUtils.NAVY_SLATE);
        }
    }
}
