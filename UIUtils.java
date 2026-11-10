import javax.swing.*;
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
}
