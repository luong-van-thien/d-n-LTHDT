import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Báo cáo Thống kê & Giám sát cảnh báo (StatisticPanel).
 */
public class StatisticPanel extends JPanel {
    private final StatisticService statisticService;
    private final ProductService productService;

    private JLabel lblTotalRevenue;
    private JLabel lblTotalProfit;
    private JLabel lblTotalInvoices;
    private JLabel lblTotalInventory;

    private JTable tblAlerts;
    private DefaultTableModel modelAlerts;

    public StatisticPanel(StatisticService statisticService, ProductService productService) {
        this.statisticService = statisticService;
        this.productService = productService;

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        refreshStatistics();
    }

    private void initComponents() {
        // --- 1. TOP: 4 KPI Cards ---
        JPanel topCards = new JPanel(new GridLayout(1, 4, 15, 0));
        topCards.setBackground(UIUtils.BG_LIGHT);

        lblTotalRevenue = new JLabel("0 đ", JLabel.CENTER);
        lblTotalProfit = new JLabel("0 đ", JLabel.CENTER);
        lblTotalInvoices = new JLabel("0", JLabel.CENTER);
        lblTotalInventory = new JLabel("0 SP", JLabel.CENTER);

        topCards.add(createKpiCard("💰 TỔNG DOANH THU", lblTotalRevenue, UIUtils.PRIMARY_COLOR));
        topCards.add(createKpiCard("📈 LỢI NHUẬN ƯỚC TÍNH", lblTotalProfit, UIUtils.SUCCESS_COLOR));
        topCards.add(createKpiCard("🧾 SỐ LƯỢNG HÓA ĐƠN", lblTotalInvoices, new Color(111, 66, 193)));
        topCards.add(createKpiCard("📦 TỔNG HÀNG TRONG KHO", lblTotalInventory, new Color(253, 126, 20)));

        add(topCards, BorderLayout.NORTH);

        // --- 2. CENTER: Bảng Cảnh Báo Nguy Cấp ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.WARNING_COLOR, 2),
                "⚠️ DANH SÁCH MẶT HÀNG CẦN CHÚ Ý (TỒN KHO <= 5 HOẶC SẮP HẾT HẠN SỬ DỤNG)",
                TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, new Color(180, 100, 0)
        ));

        String[] cols = {"Mã SP", "Tên sản phẩm", "Loại", "Tồn kho", "Giá bán", "Tình trạng cảnh báo"};
        modelAlerts = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAlerts = new JTable(modelAlerts);
        tblAlerts.setFont(UIUtils.BODY_FONT);
        tblAlerts.setRowHeight(26);
        tblAlerts.getTableHeader().setFont(UIUtils.BOLD_FONT);

        centerPanel.add(new JScrollPane(tblAlerts), BorderLayout.CENTER);

        JPanel centerBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        centerBottom.setBackground(Color.WHITE);
        JButton btnRefresh = UIUtils.createStyledButton("🔄 Cập nhật số liệu", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnRefresh.addActionListener(e -> refreshStatistics());
        centerBottom.add(btnRefresh);
        centerPanel.add(centerBottom, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // --- 3. BOTTOM: Thông tin Đồ Án & Nhóm tác giả ---
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTitle = new JLabel("🎓 ĐỒ ÁN GIỮA KỲ: HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART SYSTEM)");
        lblTitle.setFont(UIUtils.BOLD_FONT);
        lblTitle.setForeground(UIUtils.PRIMARY_COLOR);

        JLabel lblDetails = new JLabel("<html>Môn học: <b>Lập trình hướng đối tượng (Java)</b> | GV hướng dẫn: <b>ThS. Nguyễn Thị Tuyết Hải</b> (PTIT HCM)<br>" +
                "Nhóm 4 sinh viên | Áp dụng đầy đủ: OOP, Kế thừa, Đa hình, Collections, Exception, Lambda & Swing MVC</html>");
        lblDetails.setFont(UIUtils.BODY_FONT);

        infoPanel.add(lblTitle, BorderLayout.NORTH);
        infoPanel.add(lblDetails, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 230, 235), 1),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 110, 120));

        valueLabel.setFont(UIUtils.BIG_PRICE_FONT);
        valueLabel.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshStatistics() {
        lblTotalRevenue.setText(UIUtils.formatCurrency(statisticService.calculateTotalRevenue()));
        lblTotalProfit.setText(UIUtils.formatCurrency(statisticService.calculateTotalEstimatedProfit()));
        lblTotalInvoices.setText(String.valueOf(statisticService.getTotalInvoicesCount()));
        lblTotalInventory.setText(statisticService.getTotalInventoryQuantity() + " sản phẩm");

        // Cập nhật bảng cảnh báo
        modelAlerts.setRowCount(0);
        List<Product> lowStock = productService.getLowStockProducts(5);
        List<Product> expiring = productService.getExpiringProducts();

        for (Product p : lowStock) {
            String warning = (p.getQuantity() == 0) ? "HẾT HÀNG TRONG KHO (0)" : "TỒN KHO ÍT (<= 5)";
            modelAlerts.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory().getDisplayName(),
                    p.getQuantity(), UIUtils.formatCurrency(p.getSellingPrice()), warning
            });
        }

        for (Product p : expiring) {
            if (!lowStock.contains(p)) {
                modelAlerts.addRow(new Object[]{
                        p.getId(), p.getName(), p.getCategory().getDisplayName(),
                        p.getQuantity(), UIUtils.formatCurrency(p.getSellingPrice()), "SẮP HẾT HẠN SỬ DỤNG"
                });
            }
        }
    }
}

