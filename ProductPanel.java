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
    private JTextField txtFoodNsx;
    private JTextField txtFoodHsd;

    // Beverage fields
    private JTextField txtBevVolume;
    private JCheckBox chkBevAlcohol;
    private JTextField txtBevHsd;

    // Electronic fields
    private JTextField txtElecWarranty;
    private JTextField txtElecWatt;

    // Action Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

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
        btnResetFilter = UIUtils.createStyledButton("Làm mới", UIUtils.GRAY_COLOR, Color.WHITE);
        btnAlertFilter = UIUtils.createStyledButton("Cảnh báo tồn/hạn", UIUtils.WARNING_COLOR, Color.WHITE);
        btnExportCsv = UIUtils.createStyledButton("Xuất Excel/CSV", new Color(13, 110, 253), Color.WHITE);

        topPanel.add(btnSearch);
        topPanel.add(btnResetFilter);
        topPanel.add(btnAlertFilter);
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

        tblProducts = new JTable(tableModel);
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

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblProducts.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblProducts.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tblProducts.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        tblProducts.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(tblProducts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. BOTTOM PANEL: Form nhập liệu & Buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "THÔNG TIN CHI TIẾT SẢN PHẨM", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));

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
        foodPanel.add(new JLabel("Ngày SX (yyyy-MM-dd):"));
        txtFoodNsx = new JTextField(LocalDate.now().minusMonths(1).toString(), 9);
        foodPanel.add(txtFoodNsx);
        foodPanel.add(new JLabel("Hạn SD (yyyy-MM-dd):"));
        txtFoodHsd = new JTextField(LocalDate.now().plusMonths(6).toString(), 9);
        foodPanel.add(txtFoodHsd);

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
        txtBevHsd = new JTextField(LocalDate.now().plusMonths(9).toString(), 9);
        bevPanel.add(txtBevHsd);

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
        btnClear = UIUtils.createStyledButton("Xóa trắng form", UIUtils.GRAY_COLOR, Color.WHITE);

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
    public JButton getBtnExportCsv() { return btnExportCsv; }

    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtName() { return txtName; }
    public JComboBox<Category> getCboCategory() { return cboCategory; }
    public JTextField getTxtImportPrice() { return txtImportPrice; }
    public JTextField getTxtSellingPrice() { return txtSellingPrice; }
    public JTextField getTxtQuantity() { return txtQuantity; }

    public JTextField getTxtFoodNsx() { return txtFoodNsx; }
    public JTextField getTxtFoodHsd() { return txtFoodHsd; }
    public JTextField getTxtBevVolume() { return txtBevVolume; }
    public JCheckBox getChkBevAlcohol() { return chkBevAlcohol; }
    public JTextField getTxtBevHsd() { return txtBevHsd; }
    public JTextField getTxtElecWarranty() { return txtElecWarranty; }
    public JTextField getTxtElecWatt() { return txtElecWatt; }

    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear() { return btnClear; }
    public CardLayout getExtraCardLayout() { return extraCardLayout; }
    public JPanel getExtraCardPanel() { return extraCardPanel; }
}

