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

    public static final Color PRIMARY_COLOR = new Color(24, 119, 242);
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    public static final Color DANGER_COLOR = new Color(220, 53, 69);
    public static final Color WARNING_COLOR = new Color(255, 193, 7);
    public static final Color BG_LIGHT = new Color(245, 247, 250);

    private static final NumberFormat CURRENCY_FMT = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));

    public static String formatCurrency(double amount) {
        return CURRENCY_FMT.format(amount) + " đ";
    }

    public static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        return btn;
    }
}

