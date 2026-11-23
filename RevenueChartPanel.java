import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Thành phần Biểu đồ Doanh thu Trực quan (RevenueChartPanel):
 * - Được vẽ hoàn toàn bằng Graphics2D thuần (Java Standard Library), không phụ thuộc thư viện ngoài.
 * - Hỗ trợ Anti-aliasing, Gradient màu hiện đại, đổ bóng mềm và góc bo tròn thẩm mỹ.
 * - Hỗ trợ 2 chế độ hiển thị linh hoạt:
 *   1. Doanh thu theo Nhóm danh mục hàng hóa (Category Revenue Breakdown).
 *   2. Doanh thu theo 7 ngày gần nhất (Daily Revenue Trend).
 * - Hiệu ứng tương tác rê chuột (Hover Tooltip Card) hiển thị chi tiết số tiền và tỷ lệ phần trăm.
 */
public class RevenueChartPanel extends JPanel {

    public enum ChartMode {
        BY_CATEGORY,
        BY_LAST_7_DAYS
    }

    private ChartMode currentMode = ChartMode.BY_CATEGORY;

    // Data containers
    private final Map<Category, Double> categoryRevenue = new EnumMap<>(Category.class);
    private final Map<LocalDate, Double> dailyRevenue = new TreeMap<>();
    private final Map<LocalDate, Integer> dailyInvoices = new TreeMap<>();

    // Hover tooltip state
    private int hoveredBarIndex = -1;
    private Point mousePoint = null;

    // UI Buttons
    private JButton btnModeCat;
    private JButton btnModeDays;

    public RevenueChartPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        initHeader();
        initMouseListener();
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titlePanel.setOpaque(false);
        JLabel lblIcon = new JLabel(UIUtils.VectorIcons.chart(16, UIUtils.PRIMARY_COLOR));
        JLabel lblTitle = new JLabel("BIỂU ĐỒ TRỰC QUAN DOANH THU & XU HƯỚNG BÁN HÀNG");
        lblTitle.setFont(UIUtils.HEADER_FONT);
        lblTitle.setForeground(UIUtils.NAVY_SLATE);
        titlePanel.add(lblIcon);
        titlePanel.add(lblTitle);
        header.add(titlePanel, BorderLayout.WEST);

        // Segment switch buttons
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        togglePanel.setOpaque(false);

        btnModeCat = createModeButton("Theo Nhóm Hàng", true);
        btnModeDays = createModeButton("7 Ngày Gần Nhất", false);

        btnModeCat.addActionListener(e -> {
            currentMode = ChartMode.BY_CATEGORY;
            btnModeCat.setBackground(UIUtils.PRIMARY_COLOR);
            btnModeCat.setForeground(Color.WHITE);
            btnModeDays.setBackground(Color.WHITE);
            btnModeDays.setForeground(UIUtils.NAVY_SLATE);
            repaint();
        });

        btnModeDays.addActionListener(e -> {
            currentMode = ChartMode.BY_LAST_7_DAYS;
            btnModeDays.setBackground(UIUtils.PRIMARY_COLOR);
            btnModeDays.setForeground(Color.WHITE);
            btnModeCat.setBackground(Color.WHITE);
            btnModeCat.setForeground(UIUtils.NAVY_SLATE);
            repaint();
        });

        togglePanel.add(btnModeCat);
        togglePanel.add(btnModeDays);
        header.add(togglePanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private JButton createModeButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_DARK, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        if (active) {
            btn.setBackground(UIUtils.PRIMARY_COLOR);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(UIUtils.NAVY_SLATE);
        }
        return btn;
    }

    private void initMouseListener() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                checkHover(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredBarIndex = -1;
                mousePoint = null;
                repaint();
            }
        };
        addMouseMotionListener(ma);
        addMouseListener(ma);
    }

    private final List<Rectangle> barBounds = new ArrayList<>();
    private final List<String> barTooltips = new ArrayList<>();

    private void checkHover(Point p) {
        int oldHover = hoveredBarIndex;
        hoveredBarIndex = -1;
        for (int i = 0; i < barBounds.size(); i++) {
            if (barBounds.get(i).contains(p)) {
                hoveredBarIndex = i;
                break;
            }
        }
        if (oldHover != hoveredBarIndex) {
            repaint();
        }
    }

    /**
     * Cập nhật dữ liệu từ InvoiceRepository và tính toán doanh thu.
     */
    public void updateData(List<Invoice> invoices) {
        categoryRevenue.clear();
        for (Category cat : Category.values()) {
            categoryRevenue.put(cat, 0.0);
        }

        dailyRevenue.clear();
        dailyInvoices.clear();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            dailyRevenue.put(day, 0.0);
            dailyInvoices.put(day, 0);
        }

        if (invoices != null) {
            for (Invoice inv : invoices) {
                // Doanh thu theo ngày
                LocalDate invDate = inv.getCreatedAt().toLocalDate();
                if (dailyRevenue.containsKey(invDate)) {
                    dailyRevenue.put(invDate, dailyRevenue.get(invDate) + inv.getFinalAmount());
                    dailyInvoices.put(invDate, dailyInvoices.get(invDate) + 1);
                }

                // Doanh thu theo category
                for (InvoiceItem item : inv.getItems()) {
                    Category cat = item.getProduct().getCategory();
                    categoryRevenue.put(cat, categoryRevenue.getOrDefault(cat, 0.0) + item.getSubTotal());
                }
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntialiasing(g2);

        int panelW = getWidth();
        int panelH = getHeight();
        int topOffset = 38; // Dưới header
        int chartH = panelH - topOffset - 35; // Chừa chỗ cho nhãn trục X
        int chartW = panelW - 90; // Chừa chỗ cho trục Y và lề phải
        int chartX = 75;
        int chartY = topOffset + 10;

        barBounds.clear();
        barTooltips.clear();

        if (chartW <= 50 || chartH <= 50) {
            g2.dispose();
            return;
        }

        // 1. Vẽ nền biểu đồ
        g2.setColor(new Color(248, 250, 252));
        g2.fillRoundRect(chartX - 10, chartY - 5, chartW + 20, chartH + 10, 10, 10);
        g2.setColor(new Color(226, 232, 240));
        g2.drawRoundRect(chartX - 10, chartY - 5, chartW + 20, chartH + 10, 10, 10);

        // 2. Xác định giá trị cực đại để chia tỷ lệ trục Y
        double maxVal = 100_000.0;
        if (currentMode == ChartMode.BY_CATEGORY) {
            for (double v : categoryRevenue.values()) {
                if (v > maxVal) maxVal = v;
            }
        } else {
            for (double v : dailyRevenue.values()) {
                if (v > maxVal) maxVal = v;
            }
        }

        // Làm tròn mốc maxVal lên mức dễ nhìn (bội số đẹp)
        maxVal = roundNiceMax(maxVal);

        // 3. Vẽ các đường lưới ngang (Grid lines) & Nhãn trục Y
        int gridSteps = 4;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        for (int i = 0; i <= gridSteps; i++) {
            double levelVal = maxVal * i / gridSteps;
            int yPos = chartY + chartH - (int) (chartH * (i / (double) gridSteps));

            // Đường lưới nét đứt mờ
            g2.setColor(new Color(230, 235, 243));
            g2.drawLine(chartX, yPos, chartX + chartW, yPos);

            // Nhãn số tiền trục Y
            g2.setColor(UIUtils.SLATE_TEXT);
            String yLabel = formatCompact(levelVal);
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(yLabel);
            g2.drawString(yLabel, chartX - strW - 10, yPos + 4);
        }

        // 4. Vẽ các cột dữ liệu (Bars)
        if (currentMode == ChartMode.BY_CATEGORY) {
            drawCategoryBars(g2, chartX, chartY, chartW, chartH, maxVal);
        } else {
            drawDailyBars(g2, chartX, chartY, chartW, chartH, maxVal);
        }

        // 5. Vẽ Tooltip Card khi hover
        if (hoveredBarIndex >= 0 && hoveredBarIndex < barTooltips.size() && mousePoint != null) {
            drawTooltipCard(g2, barTooltips.get(hoveredBarIndex), mousePoint.x, mousePoint.y, panelW, panelH);
        }

        g2.dispose();
    }

    private void drawCategoryBars(Graphics2D g2, int chartX, int chartY, int chartW, int chartH, double maxVal) {
        Category[] cats = Category.values();
        int n = cats.length;
        int barW = Math.min(85, (chartW - (n + 1) * 20) / n);
        int totalSpacing = chartW - (n * barW);
        int spacing = totalSpacing / (n + 1);

        double totalAll = categoryRevenue.values().stream().mapToDouble(Double::doubleValue).sum();

        Color[] gradTop = {
                new Color(59, 130, 246),  // Xanh dương Thực phẩm
                new Color(16, 185, 129),  // Xanh lục Đồ uống
                new Color(168, 85, 247)   // Tím Điện tử
        };
        Color[] gradBot = {
                new Color(29, 78, 216),
                new Color(5, 150, 105),
                new Color(126, 34, 206)
        };

        for (int i = 0; i < n; i++) {
            Category cat = cats[i];
            double val = categoryRevenue.getOrDefault(cat, 0.0);
            double pct = totalAll > 0 ? (val / totalAll * 100.0) : 0.0;

            int bH = (int) ((val / maxVal) * chartH);
            if (bH < 4 && val > 0) bH = 4;

            int bX = chartX + spacing + i * (barW + spacing);
            int bY = chartY + chartH - bH;

            Rectangle rect = new Rectangle(bX, bY, barW, bH);
            barBounds.add(rect);

            String tip = String.format("<html><b>%s</b><br/>Doanh thu: <b>%s</b><br/>Tỷ trọng: <b>%.1f%%</b></html>",
                    cat.getDisplayName(), UIUtils.formatCurrency(val), pct);
            barTooltips.add(tip);

            // Cột đang hover sáng hơn
            boolean isHover = (hoveredBarIndex == i);

            // Vẽ thanh cột Gradient
            GradientPaint gp = new GradientPaint(bX, bY, isHover ? gradTop[i].brighter() : gradTop[i],
                    bX, bY + bH, isHover ? gradBot[i].brighter() : gradBot[i]);
            g2.setPaint(gp);
            g2.fillRoundRect(bX, bY, barW, bH, 8, 8);

            // Viền sáng
            g2.setColor(isHover ? Color.WHITE : new Color(255, 255, 255, 160));
            g2.drawRoundRect(bX, bY, barW, bH, 8, 8);

            // Giá trị trên đầu cột
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(UIUtils.NAVY_SLATE);
            String valStr = formatCompact(val);
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(valStr);
            g2.drawString(valStr, bX + (barW - strW) / 2, bY - 6);

            // Nhãn phần trăm bên trong cột (nếu cột đủ cao)
            if (bH >= 22) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(Color.WHITE);
                String pctStr = String.format("%.0f%%", pct);
                int pW = g2.getFontMetrics().stringWidth(pctStr);
                g2.drawString(pctStr, bX + (barW - pW) / 2, bY + 16);
            }

            // Nhãn tên danh mục dưới đáy
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(UIUtils.NAVY_SLATE);
            String label = cat.getDisplayName();
            int lW = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, bX + (barW - lW) / 2, chartY + chartH + 18);
        }
    }

    private void drawDailyBars(Graphics2D g2, int chartX, int chartY, int chartW, int chartH, double maxVal) {
        List<LocalDate> days = new ArrayList<>(dailyRevenue.keySet());
        int n = days.size();
        int barW = Math.min(60, (chartW - (n + 1) * 12) / n);
        int totalSpacing = chartW - (n * barW);
        int spacing = totalSpacing / (n + 1);

        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 0; i < n; i++) {
            LocalDate d = days.get(i);
            double val = dailyRevenue.getOrDefault(d, 0.0);
            int invoices = dailyInvoices.getOrDefault(d, 0);

            int bH = (int) ((val / maxVal) * chartH);
            if (bH < 4 && val > 0) bH = 4;

            int bX = chartX + spacing + i * (barW + spacing);
            int bY = chartY + chartH - bH;

            Rectangle rect = new Rectangle(bX, bY, barW, bH);
            barBounds.add(rect);

            boolean isToday = d.equals(LocalDate.now());
            String tip = String.format("<html><b>Ngày %s %s</b><br/>Doanh thu: <b>%s</b><br/>Số đơn hàng: <b>%d đơn</b></html>",
                    d.format(dayFmt), isToday ? "(Hôm nay)" : "", UIUtils.formatCurrency(val), invoices);
            barTooltips.add(tip);

            boolean isHover = (hoveredBarIndex == i);

            // Cột hôm nay màu nổi bật Emerald; các ngày trước màu Blue Slate
            Color topC = isToday ? new Color(16, 185, 129) : new Color(59, 130, 246);
            Color botC = isToday ? new Color(5, 150, 105) : new Color(30, 64, 175);

            GradientPaint gp = new GradientPaint(bX, bY, isHover ? topC.brighter() : topC,
                    bX, bY + bH, isHover ? botC.brighter() : botC);
            g2.setPaint(gp);
            g2.fillRoundRect(bX, bY, barW, bH, 6, 6);

            g2.setColor(isHover ? Color.WHITE : new Color(255, 255, 255, 140));
            g2.drawRoundRect(bX, bY, barW, bH, 6, 6);

            // Số tiền trên đầu cột
            if (val > 0) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(UIUtils.NAVY_SLATE);
                String valStr = formatCompact(val);
                int strW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, bX + (barW - strW) / 2, bY - 5);
            }

            // Nhãn ngày dưới trục X
            g2.setFont(new Font("Segoe UI", isToday ? Font.BOLD : Font.PLAIN, 11));
            g2.setColor(isToday ? UIUtils.PRIMARY_COLOR : UIUtils.SLATE_TEXT);
            String dayStr = d.format(dayFmt);
            int lW = g2.getFontMetrics().stringWidth(dayStr);
            g2.drawString(dayStr, bX + (barW - lW) / 2, chartY + chartH + 18);
        }
    }

    private void drawTooltipCard(Graphics2D g2, String htmlText, int mouseX, int mouseY, int maxW, int maxH) {
        JLabel renderLbl = new JLabel(htmlText);
        renderLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        Dimension pref = renderLbl.getPreferredSize();
        int tipW = pref.width + 16;
        int tipH = pref.height + 12;

        int tipX = mouseX + 12;
        int tipY = mouseY - tipH - 8;

        if (tipX + tipW > maxW - 10) tipX = mouseX - tipW - 8;
        if (tipY < 10) tipY = mouseY + 15;

        // Bóng đổ mờ
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(tipX + 2, tipY + 2, tipW, tipH, 8, 8);

        // Nền thẻ tooltip
        g2.setColor(new Color(15, 23, 42)); // Navy dark sang trọng
        g2.fillRoundRect(tipX, tipY, tipW, tipH, 8, 8);
        g2.setColor(new Color(51, 65, 85));
        g2.drawRoundRect(tipX, tipY, tipW, tipH, 8, 8);

        renderLbl.setForeground(Color.WHITE);
        renderLbl.setSize(pref);
        Graphics gClip = g2.create(tipX + 8, tipY + 6, pref.width, pref.height);
        renderLbl.paint(gClip);
        gClip.dispose();
    }

    private double roundNiceMax(double val) {
        if (val <= 100_000) return 100_000;
        if (val <= 500_000) return 500_000;
        if (val <= 1_000_000) return 1_000_000;
        if (val <= 2_000_000) return 2_000_000;
        if (val <= 5_000_000) return 5_000_000;
        if (val <= 10_000_000) return 10_000_000;
        if (val <= 20_000_000) return 20_000_000;
        if (val <= 50_000_000) return 50_000_000;
        return Math.ceil(val / 10_000_000.0) * 10_000_000.0;
    }

    private String formatCompact(double val) {
        if (val >= 1_000_000) {
            double m = val / 1_000_000.0;
            return (m == (long) m) ? String.format("%.0f tr", m) : String.format("%.1f tr", m);
        } else if (val >= 1_000) {
            double k = val / 1_000.0;
            return (k == (long) k) ? String.format("%.0f k", k) : String.format("%.0f k", k);
        } else {
            return String.format("%.0f", val);
        }
    }
}
