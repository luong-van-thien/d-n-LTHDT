import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Lớp tiện ích giao diện (UIUtils) hỗ trợ format tiền tệ và style giao diện.
 */
public class UIUtils {
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font BIG_PRICE_FONT = new Font("Segoe UI", Font.BOLD, 20);

    // Màu sắc đậm đà, sắc nét, rõ ràng 100%
    public static final Color PRIMARY_COLOR = new Color(13, 110, 253);   // Xanh dương đậm
    public static final Color SUCCESS_COLOR = new Color(25, 135, 84);    // Xanh lá đậm
    public static final Color DANGER_COLOR = new Color(220, 53, 69);     // Đỏ đậm
    public static final Color WARNING_COLOR = new Color(217, 119, 6);    // Cam vàng đậm dễ đọc
    public static final Color GRAY_COLOR = new Color(75, 85, 99);        // Xám than rõ ràng
    public static final Color BG_LIGHT = new Color(245, 247, 250);

    private static final NumberFormat CURRENCY_FMT = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));

    public static String formatCurrency(double amount) {
        return CURRENCY_FMT.format(amount) + " đ";
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
     * Hỗ trợ tìm kiếm từ khóa bất kỳ (chỉ cần xuất hiện trong chuỗi nguồn).
     * Ví dụ: gõ "m" -> khớp mọi món có 'm' hoặc 'M'
     *        gõ "mi" -> khớp cả "Mì tôm", "Bánh mì", "Kẹo Milo"
     */
    public static boolean containsIgnoreCaseAndAccent(String source, String query) {
        if (query == null || query.trim().isEmpty()) return true;
        if (source == null) return false;

        String cleanSource = removeAccents(source);
        String cleanQuery = removeAccents(query);

        return cleanSource.contains(cleanQuery) || source.toLowerCase().contains(query.toLowerCase().trim());
    }

    /**
     * Tạo nút bấm có màu nền sắc nét, viền bo tròn hiện đại, hiển thị rõ ràng 100% không bị mờ.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                // Vẽ nền màu đặc, bo tròn góc 6px
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setFont(BOLD_FONT);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        return btn;
    }

    /**
     * Tạo TableCellRenderer hỗ trợ hiển thị ToolTip đầy đủ nội dung khi rê chuột vào ô.
     * Giải quyết triệt để vấn đề tên sản phẩm hoặc thông số kỹ thuật quá dài bị cắt bớt bằng dấu '...'.
     */
    public static javax.swing.table.DefaultTableCellRenderer createToolTipRenderer(int alignment) {
        javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && !value.toString().trim().isEmpty()) {
                    setToolTipText("<html><body style='padding: 3px; font-family: Segoe UI; font-size: 11px;'>" + value.toString() + "</body></html>");
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
     * Tạo một JTable thông minh hỗ trợ cơ chế chọn/hủy chọn Toggle tự nhiên:
     * - Bấm lần 1 vào dòng: Dòng được chọn và sáng màu xanh.
     * - Bấm lần 2 vào chính dòng đó: Hủy chọn dòng, tắt vệt sáng màu xanh (Toggle).
     * - Nhấp đúp (Double click): Giữ nguyên chọn dòng để các sự kiện nhấp đúp (như thêm giỏ hàng) kích hoạt bình thường.
     */
    public static JTable createCustomTable(TableModel model) {
        JTable table = new JTable(model) {
            private int lastSelectedRow = -1;
            private long lastClickTime = 0;

            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                long now = System.currentTimeMillis();
                if (isRowSelected(rowIndex)) {
                    // Nếu nhấp đúp chuột cực nhanh (< 400ms) thì giữ nguyên dòng chọn
                    if (rowIndex == lastSelectedRow && (now - lastClickTime) < 400) {
                        super.changeSelection(rowIndex, columnIndex, toggle, extend);
                    } else {
                        // Click lần 2 có chủ đích -> Hủy chọn dòng (tắt màu xanh)
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
        return table;
    }

    /**
     * Cài đặt tính năng hủy chọn dòng khi bấm vào khoảng trống bên dưới các dòng hoặc thanh cuộn viewport:
     * - Bấm vào khoảng trống màu trắng bên dưới các dòng -> Tự động hủy chọn (tắt màu xanh).
     * - Bấm ra thanh cuộn hoặc vùng đệm viewport -> Tự động hủy chọn.
     */
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
}
