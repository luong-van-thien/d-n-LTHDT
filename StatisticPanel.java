import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Giao diện Báo cáo Thống kê & Giám sát cảnh báo (StatisticPanel).
 * Bố cục Dashboard chuyên nghiệp, đầy đủ số liệu phân tích tài chính & kho hàng.
 */
public class StatisticPanel extends JPanel {
    private final StatisticService statisticService;
    private final ProductService productService;

    // Filter time range
    private JComboBox<String> cboTimeRange;

    // Top 4 KPI Cards
    private JLabel lblTotalRevenue;
    private JLabel lblTotalProfit;
    private JLabel lblTotalInvoices;
    private JLabel lblTotalInventory;

    // Table 1: Category
    private JTable tblCategorySummary;
    private DefaultTableModel modelCategorySummary;

    // Table 2: Alerts
    private JTable tblAlerts;
    private DefaultTableModel modelAlerts;

    // Bottom Analytical Cards
    private JLabel lblTotalCapital;
    private JLabel lblRetailExpectation;
    private JLabel lblProfitRate;

    private JLabel lblSafeStock;
    private JLabel lblLowStock;
    private JLabel lblOutOfStock;

    private JLabel lblAvgOrderValue;
    private JLabel lblTotalDiscount;

    public StatisticPanel(StatisticService statisticService, ProductService productService) {
        this.statisticService = statisticService;
        this.productService = productService;

        setLayout(new BorderLayout(12, 12));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        initComponents();
        refreshStatistics();
    }

    private void initComponents() {
        // =========================================================================
        // 1. TOP: THANH LỌC THỜI GIAN & 4 THẺ KPI TỔNG QUAN
        // =========================================================================
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setBackground(UIUtils.BG_LIGHT);
        filterBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel lblDashTitle = new JLabel("TỔNG QUAN TÌNH HÌNH KINH DOANH & KHO HÀNG");
        lblDashTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDashTitle.setForeground(UIUtils.PRIMARY_COLOR);
        filterBar.add(lblDashTitle, BorderLayout.WEST);

        JPanel rightFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightFilter.setBackground(UIUtils.BG_LIGHT);
        rightFilter.add(new JLabel("Khoảng thời gian:"));
        cboTimeRange = new JComboBox<>(new String[]{
                "Toàn bộ thời gian (Tất cả)",
                "Hôm nay",
                "7 ngày qua (Tuần này)",
                "Tháng này"
        });
        cboTimeRange.setFont(UIUtils.BOLD_FONT);
        cboTimeRange.addActionListener(e -> refreshStatistics());
        rightFilter.add(cboTimeRange);
        filterBar.add(rightFilter, BorderLayout.EAST);

        JPanel topCards = new JPanel(new GridLayout(1, 4, 12, 0));
        topCards.setBackground(UIUtils.BG_LIGHT);

        lblTotalRevenue = new JLabel("0 đ", JLabel.CENTER);
        lblTotalProfit = new JLabel("0 đ", JLabel.CENTER);
        lblTotalInvoices = new JLabel("0", JLabel.CENTER);
        lblTotalInventory = new JLabel("0 SP", JLabel.CENTER);

        topCards.add(createKpiCard("TỔNG DOANH THU", lblTotalRevenue, UIUtils.PRIMARY_COLOR, "Đã thu từ bán lẻ"));
        topCards.add(createKpiCard("LỢI NHUẬN THỰC TẾ", lblTotalProfit, UIUtils.SUCCESS_COLOR, "Lãi ròng sau chiết khấu"));
        topCards.add(createKpiCard("SỐ LƯỢNG HÓA ĐƠN", lblTotalInvoices, new Color(111, 66, 193), "Giao dịch thành công"));
        topCards.add(createKpiCard("TỔNG HÀNG TRONG KHO", lblTotalInventory, new Color(253, 126, 20), "Số lượng sản phẩm còn lại"));

        JPanel topContainer = new JPanel(new BorderLayout(0, 6));
        topContainer.setBackground(UIUtils.BG_LIGHT);
        topContainer.add(filterBar, BorderLayout.NORTH);
        topContainer.add(topCards, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);

        // =========================================================================
        // 2. CENTER: BẢNG DỮ LIỆU & CẢNH BÁO (Cao cân đối 220px)
        // =========================================================================
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 12, 0));
        centerGrid.setBackground(UIUtils.BG_LIGHT);
        centerGrid.setPreferredSize(new Dimension(0, 220));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // 2A. BẢNG CƠ CẤU DANH MỤC HÀNG HÓA
        JPanel pnlCategory = new JPanel(new BorderLayout(5, 5));
        pnlCategory.setBackground(Color.WHITE);
        pnlCategory.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "CƠ CẤU HÀNG HÓA THEO DANH MỤC",
                TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));

        String[] catCols = {"Nhóm hàng", "Số mặt hàng", "Tổng tồn kho", "Tổng vốn tồn kho"};
        modelCategorySummary = new DefaultTableModel(catCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCategorySummary = UIUtils.createCustomTable(modelCategorySummary);
        tblCategorySummary.setFont(UIUtils.BODY_FONT);
        tblCategorySummary.setRowHeight(28);
        tblCategorySummary.getTableHeader().setFont(UIUtils.BOLD_FONT);
        tblCategorySummary.getTableHeader().setReorderingAllowed(false);
        tblCategorySummary.getColumnModel().getColumn(0).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblCategorySummary.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblCategorySummary.getColumnModel().getColumn(2).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblCategorySummary.getColumnModel().getColumn(3).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));

        JScrollPane scrollCat = new JScrollPane(tblCategorySummary);
        pnlCategory.add(scrollCat, BorderLayout.CENTER);
        UIUtils.installSmartDeselection(tblCategorySummary, scrollCat);
        centerGrid.add(pnlCategory);

        // 2B. BẢNG CẢNH BÁO TỒN KHO & HẠN DÙNG
        JPanel pnlAlerts = new JPanel(new BorderLayout(5, 5));
        pnlAlerts.setBackground(Color.WHITE);
        pnlAlerts.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.WARNING_COLOR, 2),
                "CẢNH BÁO HÀNG HÓA CẦN CHÚ Ý (TỒN KHO <= 5 HOẶC HẠN DÙNG)",
                TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, new Color(180, 100, 0)
        ));

        String[] alertCols = {"Mã SP", "Tên sản phẩm", "Danh mục", "Tồn", "Giá bán", "Tình trạng cảnh báo"};
        modelAlerts = new DefaultTableModel(alertCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAlerts = UIUtils.createCustomTable(modelAlerts);
        tblAlerts.setFont(UIUtils.BODY_FONT);
        tblAlerts.setRowHeight(26);
        tblAlerts.getTableHeader().setFont(UIUtils.BOLD_FONT);
        tblAlerts.getTableHeader().setReorderingAllowed(false);

        tblAlerts.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblAlerts.getColumnModel().getColumn(1).setPreferredWidth(190); // Rộng rãi, không bao giờ bị cắt chữ
        tblAlerts.getColumnModel().getColumn(2).setPreferredWidth(95);
        tblAlerts.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblAlerts.getColumnModel().getColumn(4).setPreferredWidth(85);
        tblAlerts.getColumnModel().getColumn(5).setPreferredWidth(140);

        tblAlerts.getColumnModel().getColumn(0).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblAlerts.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblAlerts.getColumnModel().getColumn(2).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblAlerts.getColumnModel().getColumn(3).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblAlerts.getColumnModel().getColumn(4).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblAlerts.getColumnModel().getColumn(5).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));

        JScrollPane scrollAlerts = new JScrollPane(tblAlerts);
        pnlAlerts.add(scrollAlerts, BorderLayout.CENTER);
        UIUtils.installSmartDeselection(tblAlerts, scrollAlerts);

        // Hủy chọn chéo giữa 2 bảng để không bao giờ bị tình trạng 2 bảng cùng sáng màu xanh cùng lúc
        tblCategorySummary.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblCategorySummary.getSelectedRow() != -1) {
                tblAlerts.clearSelection();
            }
        });
        tblAlerts.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblAlerts.getSelectedRow() != -1) {
                tblCategorySummary.clearSelection();
            }
        });

        // Bấm ra vùng trống của Panel -> Tự động hủy chọn dòng ở cả 2 bảng
        java.awt.event.MouseAdapter clearBothListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                tblCategorySummary.clearSelection();
                tblAlerts.clearSelection();
            }
        };
        pnlCategory.addMouseListener(clearBothListener);
        pnlAlerts.addMouseListener(clearBothListener);
        centerGrid.addMouseListener(clearBothListener);
        this.addMouseListener(clearBothListener);

        centerGrid.add(pnlAlerts);

        // =========================================================================
        // 3. BOTTOM: CÁC THẺ PHÂN TÍCH TÀI CHÍNH & VẬN HÀNH (Khắc phục hoàn toàn khoảng trống)
        // =========================================================================
        JPanel bottomGrid = new JPanel(new GridLayout(1, 3, 12, 0));
        bottomGrid.setBackground(UIUtils.BG_LIGHT);
        bottomGrid.setPreferredSize(new Dimension(0, 150));

        // Card 3A: Vốn Kho & Doanh Số Kỳ Vọng
        JPanel pnlFin = createAnalysisSection("Phân Tích Tài Sản Kho Hàng");
        lblTotalCapital = new JLabel("0 đ", JLabel.RIGHT);
        lblRetailExpectation = new JLabel("0 đ", JLabel.RIGHT);
        lblProfitRate = new JLabel("0%", JLabel.RIGHT);
        lblTotalCapital.setFont(UIUtils.BOLD_FONT);
        lblRetailExpectation.setFont(UIUtils.BOLD_FONT);
        lblProfitRate.setFont(UIUtils.BOLD_FONT);
        lblProfitRate.setForeground(UIUtils.SUCCESS_COLOR);

        pnlFin.add(createMiniRow("Tổng vốn nhập kho:", lblTotalCapital));
        pnlFin.add(Box.createVerticalStrut(4));
        pnlFin.add(createMiniRow("Giá trị bán lẻ kỳ vọng:", lblRetailExpectation));
        pnlFin.add(Box.createVerticalStrut(4));
        pnlFin.add(createMiniRow("Tỷ suất lợi nhuận TB:", lblProfitRate));
        bottomGrid.add(pnlFin);

        // Card 3B: Đánh Giá Rủi Ro Tồn Kho
        JPanel pnlRisk = createAnalysisSection("Đánh Giá Rủi Ro Tồn Kho");
        lblSafeStock = new JLabel("0 mặt hàng", JLabel.RIGHT);
        lblLowStock = new JLabel("0 mặt hàng", JLabel.RIGHT);
        lblOutOfStock = new JLabel("0 mặt hàng", JLabel.RIGHT);

        lblSafeStock.setFont(UIUtils.BOLD_FONT);
        lblSafeStock.setForeground(UIUtils.SUCCESS_COLOR);
        lblLowStock.setFont(UIUtils.BOLD_FONT);
        lblLowStock.setForeground(UIUtils.WARNING_COLOR);
        lblOutOfStock.setFont(UIUtils.BOLD_FONT);
        lblOutOfStock.setForeground(UIUtils.DANGER_COLOR);

        pnlRisk.add(createMiniRow("Hàng tồn kho an toàn (>5):", lblSafeStock));
        pnlRisk.add(Box.createVerticalStrut(4));
        pnlRisk.add(createMiniRow("Hàng sắp hết (<=5):", lblLowStock));
        pnlRisk.add(Box.createVerticalStrut(4));
        pnlRisk.add(createMiniRow("Hàng hết trong kho (0):", lblOutOfStock));
        bottomGrid.add(pnlRisk);

        // Card 3C: Hiệu Suất Bán Lẻ & Chiết Khấu
        JPanel pnlSales = createAnalysisSection("Hiệu Suất Bán Lẻ & Khách Hàng");
        lblAvgOrderValue = new JLabel("0 đ", JLabel.RIGHT);
        lblTotalDiscount = new JLabel("0 đ", JLabel.RIGHT);
        JLabel lblMembership = new JLabel("Tự động theo hóa đơn", JLabel.RIGHT);

        lblAvgOrderValue.setFont(UIUtils.BOLD_FONT);
        lblAvgOrderValue.setForeground(UIUtils.PRIMARY_COLOR);
        lblTotalDiscount.setFont(UIUtils.BOLD_FONT);
        lblTotalDiscount.setForeground(UIUtils.DANGER_COLOR);
        lblMembership.setFont(UIUtils.BODY_FONT);

        pnlSales.add(createMiniRow("Giá trị TB / Hóa đơn:", lblAvgOrderValue));
        pnlSales.add(Box.createVerticalStrut(4));
        pnlSales.add(createMiniRow("Tổng chiết khấu ưu đãi:", lblTotalDiscount));
        pnlSales.add(Box.createVerticalStrut(4));
        pnlSales.add(createMiniRow("Chính sách khách thân thiết:", lblMembership));
        bottomGrid.add(pnlSales);

        // =========================================================================
        // TỔNG HỢP VÀ FOOTER
        // =========================================================================
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(UIUtils.BG_LIGHT);

        mainContent.add(centerGrid);
        mainContent.add(Box.createVerticalStrut(10));
        mainContent.add(bottomGrid);

        add(mainContent, BorderLayout.CENTER);

        // Footer: Action Bar & Thông tin đề tài
        JPanel footerPanel = new JPanel(new BorderLayout(10, 0));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        JLabel lblInfo = new JLabel("<html><b>ĐỒ ÁN OOP (JAVA) - THS. NGUYỄN THỊ TUYẾT HẢI (PTIT HCM)</b> | " +
                "Nhóm 4 SV: Lương Văn Thiện (Trưởng nhóm), Trần Nhật Nguyên, Nguyễn Hải Phong, Ngô Đức Thành</html>");
        lblInfo.setFont(UIUtils.BODY_FONT);
        footerPanel.add(lblInfo, BorderLayout.WEST);

        JPanel footerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footerBtns.setBackground(Color.WHITE);
        JButton btnExportCsv = UIUtils.createStyledButton("Xuất Excel/CSV Thống Kê", UIUtils.PRIMARY_COLOR, Color.WHITE);
        JButton btnRefresh = UIUtils.createStyledButton("Cập nhật số liệu", UIUtils.SUCCESS_COLOR, Color.WHITE);

        btnRefresh.addActionListener(e -> refreshStatistics());
        btnExportCsv.addActionListener(e -> exportStatisticToCsv());

        footerBtns.add(btnExportCsv);
        footerBtns.add(btnRefresh);
        footerPanel.add(footerBtns, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(4, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 230, 235), 1),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(90, 100, 110));

        valueLabel.setFont(UIUtils.BIG_PRICE_FONT);
        valueLabel.setForeground(accentColor);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(130, 140, 150));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAnalysisSection(String title) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(215, 220, 230), 1),
                        title, TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), UIUtils.PRIMARY_COLOR
                ),
                BorderFactory.createEmptyBorder(6, 12, 8, 12)
        ));
        return pnl;
    }

    private JPanel createMiniRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.BODY_FONT);
        lbl.setForeground(new Color(70, 80, 90));

        row.add(lbl, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    public void refreshStatistics() {
        int timeMode = (cboTimeRange != null) ? cboTimeRange.getSelectedIndex() : 0;
        double revenue = statisticService.calculateTotalRevenue(timeMode);
        double profit = statisticService.calculateTotalEstimatedProfit(timeMode);
        int invoices = statisticService.getTotalInvoicesCount(timeMode);
        int inventory = statisticService.getTotalInventoryQuantity();

        lblTotalRevenue.setText(UIUtils.formatCurrency(revenue));
        lblTotalProfit.setText(UIUtils.formatCurrency(profit));
        lblTotalInvoices.setText(String.valueOf(invoices));
        lblTotalInventory.setText(inventory + " sản phẩm");

        // 1. Cập nhật bảng cơ cấu danh mục
        modelCategorySummary.setRowCount(0);
        for (Category cat : Category.values()) {
            long count = statisticService.getCountByCategory(cat);
            int qty = statisticService.getQuantityByCategory(cat);
            double val = statisticService.getInventoryValueByCategory(cat);
            modelCategorySummary.addRow(new Object[]{
                    cat.getDisplayName(),
                    count + " loại",
                    qty + " món",
                    UIUtils.formatCurrency(val)
            });
        }

        // 2. Cập nhật bảng cảnh báo
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

        // 3. Cập nhật các chỉ số phân tích bổ sung
        double totalCapital = statisticService.calculateTotalInventoryValue();
        double totalRetailExpectation = statisticService.calculateTotalRetailValue();
        double profitRate = (totalCapital > 0) ? ((totalRetailExpectation - totalCapital) / totalCapital) * 100 : 0.0;

        lblTotalCapital.setText(UIUtils.formatCurrency(totalCapital));
        lblRetailExpectation.setText(UIUtils.formatCurrency(totalRetailExpectation));
        lblProfitRate.setText(String.format("+ %.1f %%", profitRate));

        lblSafeStock.setText(statisticService.getSafeStockCount() + " mặt hàng");
        lblLowStock.setText(statisticService.getLowStockCount(5) + " mặt hàng");
        lblOutOfStock.setText(statisticService.getOutOfStockCount() + " mặt hàng");

        lblAvgOrderValue.setText(UIUtils.formatCurrency(statisticService.getAverageOrderValue(timeMode)));
        lblTotalDiscount.setText(UIUtils.formatCurrency(statisticService.getTotalDiscountGiven(timeMode)));
    }

    private void exportStatisticToCsv() {
        File exportFile = new File("BaoCao_ThongKe_TongHop.csv");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(exportFile), StandardCharsets.UTF_8))) {
            writer.write("\uFEFF"); // UTF-8 BOM
            writer.write("CHỈ SỐ THỐNG KÊ,GIÁ TRỊ,ĐƠN VỊ\n");
            writer.write(String.format("\"Tổng Doanh Thu\",%.0f,\"VNĐ\"\n", statisticService.calculateTotalRevenue()));
            writer.write(String.format("\"Lợi Nhuận Ước Tính\",%.0f,\"VNĐ\"\n", statisticService.calculateTotalEstimatedProfit()));
            writer.write(String.format("\"Tổng Số Hóa Đơn\",%d,\"Đơn hàng\"\n", statisticService.getTotalInvoicesCount()));
            writer.write(String.format("\"Tổng Số Hàng Tồn Kho\",%d,\"Sản phẩm\"\n", statisticService.getTotalInventoryQuantity()));
            writer.write(String.format("\"Tổng Vốn Tồn Kho\",%.0f,\"VNĐ\"\n", statisticService.calculateTotalInventoryValue()));
            writer.write(String.format("\"Giá Trị Bán Lẻ Dự Kiến\",%.0f,\"VNĐ\"\n", statisticService.calculateTotalRetailValue()));

            writer.write("\nDANH MỤC HÀNG HÓA,SỐ LOẠI,TỔNG TỒN KHO,TỔNG GIÁ TRỊ VỐN\n");
            for (Category cat : Category.values()) {
                writer.write(String.format("\"%s\",%d,%d,%.0f\n",
                        cat.getDisplayName(),
                        statisticService.getCountByCategory(cat),
                        statisticService.getQuantityByCategory(cat),
                        statisticService.getInventoryValueByCategory(cat)));
            }

            JOptionPane.showMessageDialog(this, "Đã xuất báo cáo thống kê thành công ra tệp:\n" + exportFile.getAbsolutePath(),
                    "Xuất file thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
