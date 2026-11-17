import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Giao diện Thống Kê & Cảnh Báo Tài Chính (StatisticPanel) chuẩn Executive Analytics Dashboard:
 * - 4 Thẻ chỉ số hiệu năng (KPI Cards) phong cách SaaS hiện đại với số to, dải màu tương phản cao.
 * - Bộ đôi bảng dữ liệu: Cơ cấu danh mục & Cảnh báo nguy cấp (Huy hiệu Badge Pills).
 * - 3 Khối phân tích chuyên sâu: Tài sản kho, Rủi ro tồn kho và Hiệu suất bán lẻ.
 * - Chức năng xuất báo cáo tổng hợp ra tệp CSV hỗ trợ tiếng Việt UTF-8 BOM.
 */
public class StatisticPanel extends JPanel {
    private final StatisticService statisticService;
    private final ProductService productService;

    // Filter
    private JComboBox<String> cboTimeRange;

    // KPI Cards
    private JLabel lblTotalRevenue;
    private JLabel lblTotalProfit;
    private JLabel lblTotalInvoices;
    private JLabel lblTotalInventory;

    // Center Tables
    private JTable tblCategorySummary;
    private DefaultTableModel modelCategorySummary;
    private JTable tblAlerts;
    private DefaultTableModel modelAlerts;

    // Bottom Detailed Metrics
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

        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        initComponents();
        refreshStatistics();
    }

    private void initComponents() {
        // =========================================================================
        // 1. TOP: THANH LỌC THỜI GIAN & 4 THẺ KPI METRIC CARDS
        // =========================================================================
        JPanel topContainer = new JPanel(new BorderLayout(0, 10));
        topContainer.setOpaque(false);

        // Thanh chọn thời gian
        JPanel filterCard = UIUtils.createCardPanel(new BorderLayout(10, 0));
        JLabel lblDashTitle = new JLabel("TỔNG QUAN TÌNH HÌNH KINH DOANH & KHO HÀNG");
        lblDashTitle.setFont(UIUtils.HEADER_FONT);
        lblDashTitle.setForeground(UIUtils.NAVY_SLATE);
        lblDashTitle.setIcon(UIUtils.VectorIcons.chart(18, UIUtils.PRIMARY_COLOR));
        lblDashTitle.setIconTextGap(8);
        filterCard.add(lblDashTitle, BorderLayout.WEST);

        JPanel rightFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightFilter.setOpaque(false);
        JLabel lblRange = new JLabel("Khoảng thời gian:");
        lblRange.setFont(UIUtils.BOLD_FONT);
        lblRange.setForeground(UIUtils.NAVY_SLATE);
        rightFilter.add(lblRange);

        cboTimeRange = new JComboBox<>(new String[]{
                "Toàn bộ thời gian (Tất cả)",
                "Hôm nay",
                "7 ngày qua (Tuần này)",
                "Tháng này"
        });
        UIUtils.styleComboBox(cboTimeRange);
        cboTimeRange.addActionListener(e -> refreshStatistics());
        rightFilter.add(cboTimeRange);
        filterCard.add(rightFilter, BorderLayout.EAST);
        topContainer.add(filterCard, BorderLayout.NORTH);

        // 4 Thẻ KPI Dashboard
        JPanel topCards = new JPanel(new GridLayout(1, 4, 12, 0));
        topCards.setOpaque(false);

        lblTotalRevenue = new JLabel("0 đ", JLabel.LEFT);
        lblTotalProfit = new JLabel("0 đ", JLabel.LEFT);
        lblTotalInvoices = new JLabel("0", JLabel.LEFT);
        lblTotalInventory = new JLabel("0 SP", JLabel.LEFT);

        topCards.add(createKpiCard("TỔNG DOANH THU", lblTotalRevenue, UIUtils.PRIMARY_COLOR, "Đã thu từ bán lẻ tại quầy"));
        topCards.add(createKpiCard("LỢI NHUẬN THỰC TẾ", lblTotalProfit, UIUtils.SUCCESS_COLOR, "Lãi ròng sau chiết khấu"));
        topCards.add(createKpiCard("SỐ LƯỢNG HÓA ĐƠN", lblTotalInvoices, UIUtils.PURPLE_COLOR, "Giao dịch thành công"));
        topCards.add(createKpiCard("TỔNG HÀNG TRONG KHO", lblTotalInventory, UIUtils.WARNING_COLOR, "Số lượng sản phẩm còn lại"));

        topContainer.add(topCards, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // =========================================================================
        // 2. CENTER: BẢNG DỮ LIỆU & CẢNH BÁO TRONG 2 CARD PANELS
        // =========================================================================
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 12, 0));
        centerGrid.setOpaque(false);
        centerGrid.setPreferredSize(new Dimension(0, 230));

        // 2A. BẢNG CƠ CẤU DANH MỤC HÀNG HÓA
        JPanel pnlCategory = UIUtils.createCardPanel(new BorderLayout(0, 8));
        pnlCategory.add(UIUtils.createSectionHeader("CƠ CẤU HÀNG HÓA THEO DANH MỤC", "Tổng hợp số loại, lượng hàng và định giá vốn", UIUtils.VectorIcons.box(18, UIUtils.PRIMARY_COLOR)), BorderLayout.NORTH);

        String[] catCols = {"Nhóm hàng", "Số mặt hàng", "Tổng tồn kho", "Tổng vốn tồn kho"};
        modelCategorySummary = new DefaultTableModel(catCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCategorySummary = UIUtils.createCustomTable(modelCategorySummary);
        tblCategorySummary.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblCategorySummary.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblCategorySummary.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblCategorySummary.getColumnModel().getColumn(3).setPreferredWidth(120);

        tblCategorySummary.getColumnModel().getColumn(0).setCellRenderer(new UIUtils.BadgeCellRenderer());
        tblCategorySummary.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblCategorySummary.getColumnModel().getColumn(2).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblCategorySummary.getColumnModel().getColumn(3).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));

        JScrollPane scrollCat = new JScrollPane(tblCategorySummary);
        scrollCat.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        pnlCategory.add(scrollCat, BorderLayout.CENTER);
        UIUtils.installSmartDeselection(tblCategorySummary, scrollCat);
        centerGrid.add(pnlCategory);

        // 2B. BẢNG CẢNH BÁO TỒN KHO & HẠN DÙNG
        JPanel pnlAlerts = UIUtils.createCardPanel(new BorderLayout(0, 8));
        pnlAlerts.add(UIUtils.createSectionHeader("CẢNH BÁO HÀNG HÓA CẦN CHÚ Ý", "Mặt hàng tồn kho ít (<= 5) hoặc sắp hết hạn sử dụng", UIUtils.VectorIcons.alert(18, UIUtils.WARNING_COLOR)), BorderLayout.NORTH);

        String[] alertCols = {"Mã SP", "Tên sản phẩm", "Danh mục", "Tồn", "Giá bán", "Tình trạng cảnh báo"};
        modelAlerts = new DefaultTableModel(alertCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAlerts = UIUtils.createCustomTable(modelAlerts);
        tblAlerts.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblAlerts.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblAlerts.getColumnModel().getColumn(2).setPreferredWidth(95);
        tblAlerts.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblAlerts.getColumnModel().getColumn(4).setPreferredWidth(85);
        tblAlerts.getColumnModel().getColumn(5).setPreferredWidth(150);

        tblAlerts.getColumnModel().getColumn(0).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.CENTER));
        tblAlerts.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblAlerts.getColumnModel().getColumn(2).setCellRenderer(new UIUtils.BadgeCellRenderer());
        tblAlerts.getColumnModel().getColumn(3).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblAlerts.getColumnModel().getColumn(4).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblAlerts.getColumnModel().getColumn(5).setCellRenderer(new UIUtils.BadgeCellRenderer());

        JScrollPane scrollAlerts = new JScrollPane(tblAlerts);
        scrollAlerts.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        pnlAlerts.add(scrollAlerts, BorderLayout.CENTER);
        UIUtils.installSmartDeselection(tblAlerts, scrollAlerts);

        // Hủy chọn chéo giữa 2 bảng
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

        // Bấm ra khoảng trống hủy chọn cả 2 bảng
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
        // 3. BOTTOM: CÁC THẺ PHÂN TÍCH CHUYÊN SÂU (3 CARDS)
        // =========================================================================
        JPanel bottomGrid = new JPanel(new GridLayout(1, 3, 12, 0));
        bottomGrid.setOpaque(false);
        bottomGrid.setPreferredSize(new Dimension(0, 155));

        // Card 3A: Vốn Kho & Doanh Số Kỳ Vọng
        JPanel pnlFin = createAnalysisSection("Phân Tích Tài Sản Kho Hàng", UIUtils.VectorIcons.box(16, UIUtils.PRIMARY_COLOR));
        lblTotalCapital = new JLabel("0 đ", JLabel.RIGHT);
        lblRetailExpectation = new JLabel("0 đ", JLabel.RIGHT);
        lblProfitRate = new JLabel("0%", JLabel.RIGHT);
        lblTotalCapital.setFont(UIUtils.BOLD_FONT);
        lblTotalCapital.setForeground(UIUtils.NAVY_SLATE);
        lblRetailExpectation.setFont(UIUtils.BOLD_FONT);
        lblRetailExpectation.setForeground(UIUtils.NAVY_SLATE);
        lblProfitRate.setFont(UIUtils.BOLD_FONT);
        lblProfitRate.setForeground(UIUtils.SUCCESS_COLOR);

        pnlFin.add(createMiniRow("Tổng vốn nhập kho:", lblTotalCapital));
        pnlFin.add(Box.createVerticalStrut(6));
        pnlFin.add(createMiniRow("Giá trị bán lẻ kỳ vọng:", lblRetailExpectation));
        pnlFin.add(Box.createVerticalStrut(6));
        pnlFin.add(createMiniRow("Tỷ suất lợi nhuận TB:", lblProfitRate));
        bottomGrid.add(pnlFin);

        // Card 3B: Đánh Giá Rủi Ro Tồn Kho
        JPanel pnlRisk = createAnalysisSection("Đánh Giá Rủi Ro Tồn Kho", UIUtils.VectorIcons.alert(16, UIUtils.WARNING_COLOR));
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
        pnlRisk.add(Box.createVerticalStrut(6));
        pnlRisk.add(createMiniRow("Hàng sắp hết (<=5):", lblLowStock));
        pnlRisk.add(Box.createVerticalStrut(6));
        pnlRisk.add(createMiniRow("Hàng hết trong kho (0):", lblOutOfStock));
        bottomGrid.add(pnlRisk);

        // Card 3C: Hiệu Suất Bán Lẻ & Chiết Khấu
        JPanel pnlSales = createAnalysisSection("Hiệu Suất Bán Lẻ & Khách Hàng", UIUtils.VectorIcons.receipt(16, UIUtils.PURPLE_COLOR));
        lblAvgOrderValue = new JLabel("0 đ", JLabel.RIGHT);
        lblTotalDiscount = new JLabel("0 đ", JLabel.RIGHT);
        JLabel lblMembership = new JLabel("Tự động theo hóa đơn", JLabel.RIGHT);

        lblAvgOrderValue.setFont(UIUtils.BOLD_FONT);
        lblAvgOrderValue.setForeground(UIUtils.PRIMARY_COLOR);
        lblTotalDiscount.setFont(UIUtils.BOLD_FONT);
        lblTotalDiscount.setForeground(UIUtils.DANGER_COLOR);
        lblMembership.setFont(UIUtils.BODY_FONT);
        lblMembership.setForeground(UIUtils.SLATE_TEXT);

        pnlSales.add(createMiniRow("Giá trị TB / Hóa đơn:", lblAvgOrderValue));
        pnlSales.add(Box.createVerticalStrut(6));
        pnlSales.add(createMiniRow("Tổng chiết khấu ưu đãi:", lblTotalDiscount));
        pnlSales.add(Box.createVerticalStrut(6));
        pnlSales.add(createMiniRow("Chính sách khách thân thiết:", lblMembership));
        bottomGrid.add(pnlSales);

        // =========================================================================
        // TỔNG HỢP VÀ FOOTER
        // =========================================================================
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        mainContent.add(centerGrid);
        mainContent.add(Box.createVerticalStrut(12));
        mainContent.add(bottomGrid);

        add(mainContent, BorderLayout.CENTER);

        // Footer: Action Bar & Thông tin đề tài
        JPanel footerCard = UIUtils.createCardPanel(new BorderLayout(10, 0));

        JLabel lblInfo = new JLabel("<html><b>ĐỒ ÁN OOP (JAVA) - THS. NGUYỄN THỊ TUYẾT HẢI (PTIT HCM)</b> | " +
                "Nhóm sinh viên: Lương Văn Thiện (Trưởng nhóm), Trần Nhật Nguyên, Nguyễn Hải Phong, Ngô Đức Thành</html>");
        lblInfo.setFont(UIUtils.BODY_FONT);
        lblInfo.setForeground(UIUtils.SLATE_TEXT);
        footerCard.add(lblInfo, BorderLayout.WEST);

        JPanel footerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerBtns.setOpaque(false);
        JButton btnExportCsv = UIUtils.createStyledButton("Xuất Báo Cáo Thống Kê", UIUtils.VectorIcons.box(15, Color.WHITE), UIUtils.PRIMARY_COLOR, Color.WHITE);
        JButton btnRefresh = UIUtils.createStyledButton("Cập nhật số liệu", UIUtils.VectorIcons.refresh(15, Color.WHITE), UIUtils.SUCCESS_COLOR, Color.WHITE);

        btnRefresh.addActionListener(e -> refreshStatistics());
        btnExportCsv.addActionListener(e -> exportStatisticToCsv());

        footerBtns.add(btnExportCsv);
        footerBtns.add(btnRefresh);
        footerCard.add(footerBtns, BorderLayout.EAST);

        add(footerCard, BorderLayout.SOUTH);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor, String subtitle) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntialiasing(g2);

                int arc = 12;
                // Nền trắng
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                // Dải màu nhấn bên trái (Accent Stripe 5px)
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 8, getHeight(), arc, arc);
                g2.fillRect(4, 0, 4, getHeight());

                // Viền xám mảnh
                g2.setColor(UIUtils.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(4, 4));
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 14));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(UIUtils.SLATE_TEXT);

        valueLabel.setFont(UIUtils.BIG_PRICE_FONT);
        valueLabel.setForeground(accentColor);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(UIUtils.SMALL_FONT);
        lblSub.setForeground(UIUtils.SLATE_MUTED);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAnalysisSection(String title, Icon icon) {
        JPanel card = UIUtils.createCardPanel(new BorderLayout(0, 8));
        card.add(UIUtils.createSectionHeader(title, null, icon), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMiniRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.BODY_FONT);
        lbl.setForeground(UIUtils.NAVY_SLATE);

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
            String warning = (p.getQuantity() == 0) ? "HẾT HÀNG (0)" : "TỒN KHO ÍT (<= 5)";
            modelAlerts.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory().getDisplayName(),
                    p.getQuantity(), UIUtils.formatCurrency(p.getSellingPrice()), warning
            });
        }

        for (Product p : expiring) {
            if (!lowStock.contains(p)) {
                modelAlerts.addRow(new Object[]{
                        p.getId(), p.getName(), p.getCategory().getDisplayName(),
                        p.getQuantity(), UIUtils.formatCurrency(p.getSellingPrice()), "SẮP HẾT HẠN"
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
                    "Xuất tệp thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất tệp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
