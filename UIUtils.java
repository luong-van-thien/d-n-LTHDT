import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Lớp tiện ích giao diện hiện đại (UIUtils) chuẩn thiết kế Enterprise / SaaS:
 * - Hệ thống màu sắc chuẩn mực, hài hòa, độ tương phản cao, dịu mắt.
 * - Font chữ Segoe UI sắc nét, khử răng cưa toàn diện.
 * - Nút bấm bo tròn (Rounded Buttons) có hiệu ứng bóng và rê chuột mượt mà.
 * - TabbedPane phẳng hiện đại (Flat Tabs) với thanh chỉ báo rực rỡ, loại bỏ giao diện xám cũ kỹ.
 * - Bảng dữ liệu (JTable) chuẩn SaaS: Chiều cao hàng 32px, xen kẽ màu nền, huy hiệu (Badge Pills).
 * - Vector Icons độc quyền vẽ trực tiếp bằng Graphics2D, sắc nét 100% trên mọi màn hình, không phụ thuộc ảnh ngoài.
 */
public class UIUtils {
    // =========================================================================
    // 1. TYPOGRAPHY (FONT CHỮ CHUẨN)
    // =========================================================================
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font BIG_PRICE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HUGE_PRICE_FONT = new Font("Segoe UI", Font.BOLD, 26);

    // =========================================================================
    // 2. PALETTE MÀU SẮC HIỆN ĐẠI (ENTERPRISE / SAAS DESIGN SYSTEM)
    // =========================================================================
    // Màu chủ đạo (Primary Blue / Electric Indigo)
    public static final Color PRIMARY_COLOR = new Color(37, 99, 235);     // Blue-600 #2563EB
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);     // Blue-700
    public static final Color PRIMARY_DARK = new Color(30, 58, 138);      // Blue-900
    public static final Color PRIMARY_LIGHT = new Color(239, 246, 255);   // Blue-50

    // Màu Navy / Dark Slate cho Navbar & Tiêu đề
    public static final Color NAVY_DARK = new Color(15, 23, 42);          // Slate-900 #0F172A
    public static final Color NAVY_SLATE = new Color(30, 41, 59);         // Slate-800 #1E293B
    public static final Color SLATE_TEXT = new Color(71, 85, 105);        // Slate-600 #475569
    public static final Color SLATE_MUTED = new Color(148, 163, 184);     // Slate-400 #94A3B8

    // Màu chức năng & Huy hiệu (Badges)
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129);    // Emerald-500 #10B981
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);    // Emerald-600
    public static final Color SUCCESS_BG = new Color(236, 253, 245);      // Emerald-50
    public static final Color SUCCESS_TEXT = new Color(6, 95, 70);        // Emerald-800

    public static final Color DANGER_COLOR = new Color(239, 68, 68);      // Red-500 #EF4444
    public static final Color DANGER_HOVER = new Color(220, 38, 38);      // Red-600
    public static final Color DANGER_BG = new Color(254, 242, 242);       // Red-50
    public static final Color DANGER_TEXT = new Color(153, 27, 27);       // Red-800

    public static final Color WARNING_COLOR = new Color(245, 158, 11);    // Amber-500 #F59E0B
    public static final Color WARNING_HOVER = new Color(217, 119, 6);     // Amber-600
    public static final Color WARNING_BG = new Color(255, 251, 235);      // Amber-50
    public static final Color WARNING_TEXT = new Color(146, 64, 14);      // Amber-800

    public static final Color PURPLE_COLOR = new Color(139, 92, 246);     // Violet-500 #8B5CF6
    public static final Color PURPLE_HOVER = new Color(124, 58, 237);     // Violet-600
    public static final Color PURPLE_BG = new Color(245, 243, 255);       // Violet-50
    public static final Color PURPLE_TEXT = new Color(91, 33, 182);       // Violet-800

    public static final Color GRAY_COLOR = new Color(100, 116, 139);      // Slate-500 #64748B
    public static final Color GRAY_HOVER = new Color(71, 85, 105);

    // Bề mặt & Viền (Surfaces & Borders)
    public static final Color BG_LIGHT = new Color(241, 245, 249);        // Slate-100 #F1F5F9
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(226, 232, 240);     // Slate-200 #E2E8F0
    public static final Color BORDER_DARK = new Color(203, 213, 225);      // Slate-300 #CBD5E1

    private static final NumberFormat CURRENCY_FMT = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));

    public static String formatCurrency(double amount) {
        return CURRENCY_FMT.format(amount) + " đ";
    }

    /**
     * Bật khử răng cưa toàn diện cho đối tượng Graphics2D để chữ và hình nét căng.
     */
    public static void enableAntialiasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /**
     * Chuẩn hóa và loại bỏ dấu tiếng Việt để tìm kiếm thông minh:
     * Ví dụ: "Mì Hảo Hảo" -> "mi hao hao", "Điện gia dụng" -> "dien gia dung".
     */
    public static String removeAccents(String str) {
        if (str == null) return "";
        String normalized = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        String pattern = "\\p{InCombiningDiacriticalMarks}+";
        String withoutAccents = normalized.replaceAll(pattern, "");
        return withoutAccents.replace('đ', 'd').replace('Đ', 'D').trim().toLowerCase();
    }

    /**
     * So khớp chuỗi con không phân biệt hoa thường và không phân biệt dấu tiếng Việt.
     */
    public static boolean containsIgnoreCaseAndAccent(String source, String query) {
        if (query == null || query.trim().isEmpty()) return true;
        if (source == null) return false;

        String cleanSource = removeAccents(source);
        String cleanQuery = removeAccents(query);

        return cleanSource.contains(cleanQuery) || source.toLowerCase().contains(query.toLowerCase().trim());
    }

    // =========================================================================
    // 3. NÚT BẤM HIỆN ĐẠI (MODERN BUTTON FACTORY)
    // =========================================================================
    /**
     * Tạo nút bấm có bo góc (Round 8px), hiệu ứng bóng đổ nhẹ và đổi màu khi hover/pressed.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        return createStyledButton(text, null, bgColor, fgColor);
    }

    public static JButton createStyledButton(String text, Icon icon, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                enableAntialiasing(g2);

                int w = getWidth();
                int h = getHeight();
                int arc = 10;

                // Màu nền động theo trạng thái
                Color currentBg = bgColor;
                if (getModel().isPressed()) {
                    currentBg = new Color(
                            Math.max(0, bgColor.getRed() - 35),
                            Math.max(0, bgColor.getGreen() - 35),
                            Math.max(0, bgColor.getBlue() - 35)
                    );
                } else if (getModel().isRollover()) {
                    currentBg = new Color(
                            Math.min(255, bgColor.getRed() + 20),
                            Math.min(255, bgColor.getGreen() + 20),
                            Math.min(255, bgColor.getBlue() + 20)
                    );
                }

                // Vẽ bóng đổ nhẹ phía dưới nút
                if (!getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(0, 2, w, h - 1, arc, arc);
                }

                // Vẽ thân nút
                g2.setColor(currentBg);
                g2.fillRoundRect(0, 0, w, h - (getModel().isPressed() ? 0 : 2), arc, arc);

                // Đường viền mảnh tinh tế
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, w - 1, h - (getModel().isPressed() ? 1 : 3), arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
        }

        btn.setFont(BOLD_FONT);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    // =========================================================================
    // 4. Ô NHẬP LIỆU & COMBOBOX HIỆN ĐẠI (ROUNDED INPUTS)
    // =========================================================================
    /**
     * Viền bo tròn thông minh cho ô nhập liệu với hiệu ứng đổi viền xanh khi Focus.
     */
    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private Color normalColor;
        private Color focusColor;
        private boolean focused = false;

        public RoundedBorder(int radius, Color normalColor, Color focusColor) {
            this.radius = radius;
            this.normalColor = normalColor;
            this.focusColor = focusColor;
        }

        public void setFocused(boolean focused) {
            this.focused = focused;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableAntialiasing(g2);

            g2.setColor(focused ? focusColor : normalColor);
            g2.setStroke(new BasicStroke(focused ? 1.8f : 1.0f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 10, 6, 10);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 10;
            insets.top = 6;
            insets.right = 10;
            insets.bottom = 6;
            return insets;
        }
    }

    public static void styleTextField(JTextField tf) {
        tf.setFont(BODY_FONT);
        tf.setBackground(Color.WHITE);
        RoundedBorder border = new RoundedBorder(8, BORDER_DARK, PRIMARY_COLOR);
        tf.setBorder(border);

        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                border.setFocused(true);
                tf.repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                border.setFocused(false);
                tf.repaint();
            }
        });
    }

    public static void styleComboBox(JComboBox<?> cb) {
        cb.setFont(BODY_FONT);
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 1),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
    }

    // =========================================================================
    // 5. THẺ CHỨA PANEL BO TRÒN (MODERN CARD PANEL)
    // =========================================================================
    /**
     * Tạo một Card Panel có nền trắng, bo góc 12px và viền xám nhạt hiện đại.
     */
    public static JPanel createCardPanel() {
        return createCardPanel(new BorderLayout());
    }

    public static JPanel createCardPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                enableAntialiasing(g2);

                int arc = 12;
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        return p;
    }

    /**
     * Tạo tiêu đề cho một khối Section hoặc Card.
     */
    public static JPanel createSectionHeader(String title, String subtitle, Icon icon) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel textPanel = new JPanel(new GridLayout(subtitle != null ? 2 : 1, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(HEADER_FONT);
        lblTitle.setForeground(NAVY_SLATE);
        if (icon != null) {
            lblTitle.setIcon(icon);
            lblTitle.setIconTextGap(8);
        }
        textPanel.add(lblTitle);

        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel lblSub = new JLabel(subtitle);
            lblSub.setFont(SMALL_FONT);
            lblSub.setForeground(SLATE_TEXT);
            textPanel.add(lblSub);
        }

        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // 6. CUSTOM FLAT TABBED PANE UI (TAB CHUẨN THIẾT KẾ PHẲNG CAO CẤP)
    // =========================================================================
    public static void applyModernTabbedPaneUI(JTabbedPane tabbedPane) {
        tabbedPane.setFont(BOLD_FONT);
        tabbedPane.setBackground(BG_LIGHT);
        tabbedPane.setOpaque(true);

        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabInsets = new Insets(10, 20, 10, 20);
                selectedTabPadInsets = new Insets(0, 0, 0, 0);
                tabAreaInsets = new Insets(4, 8, 0, 8);
                contentBorderInsets = new Insets(4, 0, 0, 0);
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 40; // Chiều cao tab thoải mái, hiện đại
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 24;
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                               int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
                // Xóa bỏ nét chấm đứt (dashed line) cổ lỗ sĩ
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                enableAntialiasing(g2);

                int arc = 8;
                if (isSelected) {
                    // Tab được chọn: Nền trắng tinh tế, bo góc trên
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(x, y, w, h + 10, arc, arc);

                    // Đường chỉ báo rực rỡ ở đáy (Active Indicator Bar)
                    g2.setColor(PRIMARY_COLOR);
                    g2.fillRect(x + 4, y + h - 3, w - 8, 3);
                } else {
                    // Tab bình thường: Nền xám nhạt dịu mắt
                    g2.setColor(new Color(230, 235, 243));
                    g2.fillRoundRect(x + 1, y + 2, w - 2, h - 2, arc, arc);
                }
                g2.dispose();
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                enableAntialiasing(g2);
                int arc = 8;

                if (isSelected) {
                    g2.setColor(BORDER_DARK);
                    g2.drawRoundRect(x, y, w - 1, h + 10, arc, arc);
                } else {
                    g2.setColor(new Color(210, 218, 228));
                    g2.drawRoundRect(x + 1, y + 2, w - 3, h - 3, arc, arc);
                }
                g2.dispose();
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BORDER_COLOR);
                g2.fillRect(0, 0, tabbedPane.getWidth(), 2);
                g2.dispose();
            }
        });
    }

    // =========================================================================
    // 7. BẢNG DỮ LIỆU CHUẨN SAAS (MODERN TABLE & HUY HIỆU BADGE PILLS)
    // =========================================================================
    /**
     * Tạo TableCellRenderer hỗ trợ hiển thị ToolTip đầy đủ nội dung khi rê chuột vào ô.
     */
    public static DefaultTableCellRenderer createToolTipRenderer(int alignment) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && !value.toString().trim().isEmpty()) {
                    setToolTipText("<html><body style='padding: 4px; font-family: Segoe UI; font-size: 11px;'>"
                            + value.toString() + "</body></html>");
                } else {
                    setToolTipText(null);
                }
                return c;
            }
        };
        renderer.setHorizontalAlignment(alignment);
        return renderer;
    }

    /**
     * Renderer vẽ huy hiệu hình viên thuốc (Badge Pill) với góc bo mềm mại.
     */
    public static class BadgeCellRenderer extends DefaultTableCellRenderer {
        private Color bgColor = SUCCESS_BG;
        private Color fgColor = SUCCESS_TEXT;
        private String badgeText = "";

        public BadgeCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            badgeText = value != null ? value.toString() : "";

            if (!isSelected) {
                // Xác định màu sắc theo nội dung
                String textLower = badgeText.toLowerCase();
                if (textLower.contains("hết") || textLower.contains("nguy cấp") || textLower.contains("đỏ")) {
                    bgColor = DANGER_BG;
                    fgColor = DANGER_TEXT;
                } else if (textLower.contains("cảnh báo") || textLower.contains("sắp") || textLower.contains("ít")) {
                    bgColor = WARNING_BG;
                    fgColor = WARNING_TEXT;
                } else if (textLower.contains("thực phẩm")) {
                    bgColor = SUCCESS_BG;
                    fgColor = SUCCESS_TEXT;
                } else if (textLower.contains("đồ uống")) {
                    bgColor = PRIMARY_LIGHT;
                    fgColor = PRIMARY_COLOR;
                } else if (textLower.contains("điện")) {
                    bgColor = PURPLE_BG;
                    fgColor = PURPLE_TEXT;
                } else {
                    bgColor = new Color(241, 245, 249);
                    fgColor = SLATE_TEXT;
                }
            } else {
                bgColor = table.getSelectionBackground();
                fgColor = table.getSelectionForeground();
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (badgeText.isEmpty()) {
                super.paintComponent(g);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            enableAntialiasing(g2);

            int w = getWidth();
            int h = getHeight();
            int badgeW = Math.min(w - 12, 140);
            int badgeH = 22;
            int x = (w - badgeW) / 2;
            int y = (h - badgeH) / 2;

            g2.setColor(bgColor);
            g2.fillRoundRect(x, y, badgeW, badgeH, 12, 12);

            g2.setFont(SMALL_BOLD);
            g2.setColor(fgColor);
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(badgeText);
            int strX = (w - strW) / 2;
            int strY = y + ((badgeH - fm.getHeight()) / 2) + fm.getAscent();

            g2.drawString(badgeText, strX, strY);
            g2.dispose();
        }
    }

    /**
     * Tạo một JTable thông minh chuẩn SaaS:
     * - Chiều cao hàng 32px (thoáng mắt).
     * - Header hiện đại với nền nhạt `#F8FAFC`, chữ bold `#334155`, chiều cao 36px.
     * - Xen kẽ màu hàng (Zebra striping).
     * - Hỗ trợ hủy chọn dòng khi bấm lại lần 2 (Toggle Selection).
     */
    public static JTable createCustomTable(TableModel model) {
        JTable table = new JTable(model) {
            private int lastSelectedRow = -1;
            private long lastClickTime = 0;

            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                long now = System.currentTimeMillis();
                if (isRowSelected(rowIndex)) {
                    if (rowIndex == lastSelectedRow && (now - lastClickTime) < 400) {
                        super.changeSelection(rowIndex, columnIndex, toggle, extend);
                    } else {
                        clearSelection();
                        lastSelectedRow = -1;
                        lastClickTime = 0;
                        return;
                    }
                } else {
                    super.changeSelection(rowIndex, columnIndex, toggle, extend);
                }
                lastSelectedRow = rowIndex;
                lastClickTime = now;
            }
        };

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(BODY_FONT);
        table.setRowHeight(32); // Chiều cao hàng chuẩn SaaS
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));

        // Màu chọn dòng thanh lịch
        table.setSelectionBackground(new Color(224, 231, 255)); // Indigo-100
        table.setSelectionForeground(new Color(30, 27, 75));     // Indigo-950

        // Tùy biến Header bảng
        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD_FONT);
        header.setBackground(new Color(248, 250, 252)); // Slate-50
        header.setForeground(NAVY_SLATE);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DARK));

        return table;
    }

    public static void installSmartDeselection(JTable table, JScrollPane scrollPane) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        java.awt.event.MouseAdapter emptySpaceListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row == -1) {
                    table.clearSelection();
                }
            }
        };

        table.addMouseListener(emptySpaceListener);

        if (scrollPane != null && scrollPane.getViewport() != null) {
            scrollPane.getViewport().addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row == -1) {
                        table.clearSelection();
                    }
                }
            });
        }
    }

    // =========================================================================
    // 8. BỘ VECTOR ICONS ĐỘC QUYỀN (VẼ TRỰC TIẾP GRAPHICS2D - SẮC NÉT 100%)
    // =========================================================================
    public static class VectorIcons {
        public static Icon search(int size, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    enableAntialiasing(g2);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int r = size * 6 / 10;
                    g2.drawOval(x + 2, y + 2, r, r);
                    g2.drawLine(x + 2 + r - 1, y + 2 + r - 1, x + size - 2, y + size - 2);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return size; }
                @Override public int getIconHeight() { return size; }
            };
        }

        public static Icon cart(int size, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    enableAntialiasing(g2);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    // Tay cầm & Thùng xe
                    g2.drawLine(x + 2, y + 3, x + 5, y + 3);
                    g2.drawLine(x + 5, y + 3, x + 7, y + size - 6);
                    g2.drawLine(x + 7, y + size - 6, x + size - 2, y + size - 6);
                    g2.drawLine(x + size - 2, y + size - 6, x + size - 1, y + 6);
                    g2.drawLine(x + size - 1, y + 6, x + 6, y + 6);
                    // 2 Bánh xe
                    g2.fillOval(x + 7, y + size - 4, 3, 3);
                    g2.fillOval(x + size - 5, y + size - 4, 3, 3);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return size; }
                @Override public int getIconHeight() { return size; }
            };
        }

        public static Icon box(int size, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    enableAntialiasing(g2);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawRoundRect(x + 2, y + 2, size - 4, size - 4, 4, 4);
                    g2.drawLine(x + 2, y + size / 2, x + size - 2, y + size / 2);
                    g2.drawLine(x + size / 2, y + 2, x + size / 2, y + size / 2);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return size; }
                @Override public int getIconHeight() { return size; }
            };
        }

        public static Icon alert(int size, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    enableAntialiasing(g2);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int[] xPoints = {x + size / 2, x + size - 2, x + 2};
                    int[] yPoints = {y + 2, y + size - 2, y + size - 2};
                    g2.drawPolygon(xPoints, yPoints, 3);
                    g2.drawLine(x + size / 2, y + 6, x + size / 2, y + size - 7);
                    g2.fillOval(x + size / 2 - 1, y + size - 5, 2, 2);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return size; }
                @Override public int getIconHeight() { return size; }
            };
        }

        public static Icon refresh(int size, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    enableAntialiasing(g2);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawArc(x + 3, y + 3, size - 6, size - 6, 45, 270);
                    // Mũi tên
                    g2.drawLine(x + size - 4, y + 2, x + size - 2, y + 6);
                    g2.drawLine(x + size - 7, y + 6, x + size - 2, y + 6);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return size; }
                @Override public int getIconHeight() { return size; }
            };
        }

        public static Icon user(int size, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    enableAntialiasing(g2);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    // Đầu
                    int headR = size * 4 / 10;
                    g2.drawOval(x + (size - headR) / 2, y + 2, headR, headR);
                    // Vai
                    g2.drawArc(x + 2, y + headR + 2, size - 4, size - headR, 0, 180);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return size; }
                @Override public int getIconHeight() { return size; }
            };
        }

        public static Icon receipt(int size, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    enableAntialiasing(g2);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawRoundRect(x + 3, y + 2, size - 6, size - 4, 3, 3);
                    g2.drawLine(x + 6, y + 6, x + size - 6, y + 6);
                    g2.drawLine(x + 6, y + 10, x + size - 6, y + 10);
                    g2.drawLine(x + 6, y + 14, x + size - 8, y + 14);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return size; }
                @Override public int getIconHeight() { return size; }
            };
        }

        public static Icon chart(int size, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    enableAntialiasing(g2);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    // Cột 1, 2, 3
                    g2.drawLine(x + 4, y + size - 3, x + 4, y + size - 7);
                    g2.drawLine(x + size / 2, y + size - 3, x + size / 2, y + 4);
                    g2.drawLine(x + size - 4, y + size - 3, x + size - 4, y + 8);
                    // Trục dưới
                    g2.drawLine(x + 2, y + size - 2, x + size - 2, y + size - 2);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return size; }
                @Override public int getIconHeight() { return size; }
            };
        }
    }
}
