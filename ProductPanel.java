import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Giao diện Quản lý Kho & Sản phẩm (ProductPanel).
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

    // Food fields (Dùng JDateChooser chuẩn hóa, chặn hoàn toàn lỗi ParseException)
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
    private TitledBorder bottomBorder;

    public ProductPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        // 1. TOP PANEL: Search, Filter, Sort
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        topPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(15);
        topPanel.add(txtSearch);

        topPanel.add(new JLabel("Loại SP:"));
        cboFilterCategory = new JComboBox<>(new String[]{"-- Tất cả --", "Thực phẩm", "Đồ uống", "Đồ điện gia dụng"});
        topPanel.add(cboFilterCategory);

        topPanel.add(new JLabel("Sắp xếp:"));
        cboSort = new JComboBox<>(new String[]{
                "Mặc định (Tên A-Z)",
                "Tên Z-A",
                "Giá bán tăng dần",
                "Giá bán giảm dần",
                "Tồn kho giảm dần",
                "Lợi nhuận cao nhất"
        });
        topPanel.add(cboSort);

        btnSearch = UIUtils.createStyledButton("Lọc / Tìm", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnResetFilter = UIUtils.createStyledButton("Quay lại tất cả SP", new Color(59, 130, 246), Color.WHITE);
        btnResetFilter.setToolTipText("Bấm để hiển thị đầy đủ toàn bộ kho hàng");
        btnAlertFilter = UIUtils.createStyledButton("Cảnh báo tồn/hạn", UIUtils.WARNING_COLOR, Color.WHITE);
        btnAlertFilter.setToolTipText("Bật/Tắt chế độ xem các sản phẩm tồn kho ít (<= 5) hoặc sắp hết hạn");
        btnCreateRestockOrder = UIUtils.createStyledButton("Tạo Phiếu Nhập Hàng", new Color(194, 65, 12), Color.WHITE);
        btnExportCsv = UIUtils.createStyledButton("Xuất file báo cáo", new Color(13, 110, 253), Color.WHITE);

        topPanel.add(btnSearch);
        topPanel.add(btnResetFilter);
        topPanel.add(btnAlertFilter);
        topPanel.add(btnCreateRestockOrder);
        topPanel.add(btnExportCsv);

        add(topPanel, BorderLayout.NORTH);

        // 2. CENTER PANEL: JTable
        String[] columns = {"Mã SP", "Tên sản phẩm", "Loại", "Giá nhập", "Giá bán", "Tồn kho", "Lợi nhuận/SP", "Thông tin chi tiết / Cảnh báo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblProducts = UIUtils.createCustomTable(tableModel);
        tblProducts.setFont(UIUtils.BODY_FONT);
        tblProducts.setRowHeight(26);
        tblProducts.getTableHeader().setFont(UIUtils.BOLD_FONT);
        tblProducts.getTableHeader().setBackground(new Color(235, 240, 245));
        tblProducts.getTableHeader().setReorderingAllowed(false);
        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(210);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblProducts.getColumnModel().getColumn(3).setPreferredWidth(85);
        tblProducts.getColumnModel().getColumn(4).setPreferredWidth(85);
        tblProducts.getColumnModel().getColumn(5).setPreferredWidth(60);
        tblProducts.getColumnModel().getColumn(6).setPreferredWidth(95);
        tblProducts.getColumnModel().getColumn(7).setPreferredWidth(230);

        // Đánh dấu màu nổi bật (Highlight) cảnh báo cho các dòng nguy cấp & Thêm ToolTip đầy đủ
        tblProducts.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (value != null && !value.toString().trim().isEmpty()) {
                    setToolTipText("<html><body style='padding: 3px; font-family: Segoe UI; font-size: 11px;'>" + value.toString() + "</body></html>");
                } else {
                    setToolTipText(null);
                }

                if (!isSelected) {
                    try {
                        Object qtyObj = table.getValueAt(row, 5);
                        int qty = (qtyObj instanceof Integer) ? (Integer) qtyObj : Integer.parseInt(qtyObj.toString().trim());
                        String warningInfo = table.getValueAt(row, 7) != null ? table.getValueAt(row, 7).toString() : "";

                        if (qty == 0 || warningInfo.contains("[ĐÃ HẾT HẠN!")) {
                            c.setBackground(new Color(254, 226, 226)); // Đỏ nhạt cho hàng hết / hết hạn
                            c.setForeground(new Color(185, 28, 28));
                        } else if (qty <= 5 || warningInfo.contains("[CẢNH BÁO")) {
                            c.setBackground(new Color(254, 243, 199)); // Vàng cam cho hàng tồn ít / cận date
                            c.setForeground(new Color(180, 83, 9));
                        } else {
                            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                            c.setForeground(new Color(30, 41, 59));
                        }
                    } catch (Exception ex) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }

                if (col >= 3 && col <= 6) {
                    setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblProducts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1));
        UIUtils.installSmartDeselection(tblProducts, scrollPane);

        add(scrollPane, BorderLayout.CENTER);

        // 3. BOTTOM PANEL: Form nhập liệu & Buttons
        bottomBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "THÔNG TIN CHI TIẾT SẢN PHẨM", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        );
        bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(bottomBorder);

        JPanel formGrid = new JPanel(new GridLayout(3, 4, 10, 8));
        formGrid.setBackground(Color.WHITE);
        formGrid.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        txtId = new JTextField();
        txtName = new JTextField();
        cboCategory = new JComboBox<>(new Category[]{Category.FOOD, Category.BEVERAGE, Category.ELECTRONIC});
        txtImportPrice = new JTextField();
        txtSellingPrice = new JTextField();
        txtQuantity = new JTextField();

        formGrid.add(new JLabel("Mã sản phẩm (*):"));
        formGrid.add(txtId);
        formGrid.add(new JLabel("Tên sản phẩm (*):"));
        formGrid.add(txtName);

        formGrid.add(new JLabel("Loại sản phẩm:"));
        formGrid.add(cboCategory);
        formGrid.add(new JLabel("Số lượng tồn kho (*):"));
        formGrid.add(txtQuantity);

        formGrid.add(new JLabel("Giá nhập (VNĐ) (*):"));
        formGrid.add(txtImportPrice);
        formGrid.add(new JLabel("Giá bán (VNĐ) (*):"));
        formGrid.add(txtSellingPrice);

        // Extra dynamic Card Panel
        extraCardLayout = new CardLayout();
        extraCardPanel = new JPanel(extraCardLayout);
        extraCardPanel.setBackground(Color.WHITE);

        // Card Food
        JPanel foodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        foodPanel.setBackground(Color.WHITE);
        foodPanel.add(new JLabel("Ngày SX:"));
        dateFoodNsx = new JDateChooser(LocalDate.now().minusMonths(1));
        foodPanel.add(dateFoodNsx);
        foodPanel.add(new JLabel("Hạn SD:"));
        dateFoodHsd = new JDateChooser(LocalDate.now().plusMonths(6));
        foodPanel.add(dateFoodHsd);

        // Card Beverage
        JPanel bevPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bevPanel.setBackground(Color.WHITE);
        bevPanel.add(new JLabel("Dung tích (ml):"));
        txtBevVolume = new JTextField("330", 5);
        bevPanel.add(txtBevVolume);
        chkBevAlcohol = new JCheckBox("Có chứa cồn (bia/rượu)");
        chkBevAlcohol.setBackground(Color.WHITE);
        bevPanel.add(chkBevAlcohol);
        bevPanel.add(new JLabel("HSD:"));
        dateBevHsd = new JDateChooser(LocalDate.now().plusMonths(9));
        bevPanel.add(dateBevHsd);

        // Card Electronic
        JPanel elecPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        elecPanel.setBackground(Color.WHITE);
        elecPanel.add(new JLabel("Bảo hành (tháng):"));
        txtElecWarranty = new JTextField("12", 5);
        elecPanel.add(txtElecWarranty);
        elecPanel.add(new JLabel("Công suất (Watt):"));
        txtElecWatt = new JTextField("1000", 6);
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

        // Button Bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttonPanel.setBackground(Color.WHITE);

        btnAdd = UIUtils.createStyledButton("Thêm mới", UIUtils.SUCCESS_COLOR, Color.WHITE);
        btnUpdate = UIUtils.createStyledButton("Cập nhật", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnDelete = UIUtils.createStyledButton("Xóa", UIUtils.DANGER_COLOR, Color.WHITE);
        btnClear = UIUtils.createStyledButton("Làm mới biểu mẫu", UIUtils.GRAY_COLOR, Color.WHITE);
        btnClear.setToolTipText("Làm mới biểu mẫu và tự động điền Mã sản phẩm mới tiếp theo để thêm hàng");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(formGrid, BorderLayout.NORTH);
        bottomContainer.add(extraCardPanel, BorderLayout.CENTER);
        bottomContainer.add(buttonPanel, BorderLayout.SOUTH);

        bottomPanel.add(bottomContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
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
     * Khi bật: Khóa các thao tác sửa đổi kho (Thêm, Sửa, Xóa, Nhập hàng) để đảm bảo an toàn dữ liệu.
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

        if (bottomBorder != null && bottomPanel != null) {
            bottomBorder.setTitle(isCashier
                    ? "THÔNG TIN CHI TIẾT SẢN PHẨM [CHẾ ĐỘ THU NGÂN - CHỈ ĐỌC]"
                    : "THÔNG TIN CHI TIẾT SẢN PHẨM");
            bottomBorder.setTitleColor(isCashier ? UIUtils.WARNING_COLOR : UIUtils.PRIMARY_COLOR);
            bottomPanel.repaint();
        }
    }
}

