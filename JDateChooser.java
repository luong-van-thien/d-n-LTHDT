import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Component chọn ngày tháng trực quan (JDateChooser).
 * Tích hợp chuẩn Java Swing, không phụ thuộc thư viện ngoài (Third-party JAR).
 * Bao gồm:
 * 1. Spinner điều chỉnh ngày/tháng/năm chính xác (yyyy-MM-dd)
 * 2. Nút mở bảng lịch trực quan (Calendar Popup) chọn nhanh ngày trong tháng.
 * Chặn hoàn toàn 100% lỗi ParseException do người dùng gõ sai định dạng!
 */
public class JDateChooser extends JPanel {
    private final JSpinner dateSpinner;
    private final JButton btnCalendar;
    private final SpinnerDateModel dateModel;

    public JDateChooser() {
        this(LocalDate.now());
    }

    public JDateChooser(LocalDate initialDate) {
        setLayout(new BorderLayout(3, 0));
        setOpaque(false);

        Date init = Date.from((initialDate != null ? initialDate : LocalDate.now())
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        dateModel = new SpinnerDateModel(init, null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        dateSpinner.setFont(UIUtils.BODY_FONT);
        dateSpinner.setPreferredSize(new Dimension(105, 26));

        btnCalendar = new JButton("📅") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(220, 225, 235));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(240, 245, 255));
                } else {
                    g2.setColor(new Color(245, 247, 250));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(200, 205, 215));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCalendar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        btnCalendar.setFocusPainted(false);
        btnCalendar.setContentAreaFilled(false);
        btnCalendar.setBorderPainted(false);
        btnCalendar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalendar.setToolTipText("Bấm để mở lịch chọn ngày trực quan");
        btnCalendar.setPreferredSize(new Dimension(32, 26));

        btnCalendar.addActionListener(e -> showCalendarPopup());

        add(dateSpinner, BorderLayout.CENTER);
        add(btnCalendar, BorderLayout.EAST);
    }

    private void showCalendarPopup() {
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(ancestor instanceof Frame ? (Frame) ancestor : null, "Chọn Ngày", true);
        dialog.setSize(300, 270);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(5, 5));
        dialog.getContentPane().setBackground(Color.WHITE);

        LocalDate currentDate = getLocalDate();
        final int[] currentYear = {currentDate.getYear()};
        final int[] currentMonth = {currentDate.getMonthValue()};

        // Header: Chọn Tháng & Năm
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        header.setBackground(new Color(24, 43, 73));

        JComboBox<String> cboMonth = new JComboBox<>();
        for (int m = 1; m <= 12; m++) {
            cboMonth.addItem("Tháng " + m);
        }
        cboMonth.setSelectedIndex(currentMonth[0] - 1);

        JComboBox<Integer> cboYear = new JComboBox<>();
        for (int y = 2020; y <= 2035; y++) {
            cboYear.addItem(y);
        }
        cboYear.setSelectedItem(currentYear[0]);

        header.add(cboMonth);
        header.add(cboYear);
        dialog.add(header, BorderLayout.NORTH);

        // Center: Calendar Grid
        JPanel grid = new JPanel(new GridLayout(7, 7, 2, 2));
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        Runnable renderDays = () -> {
            grid.removeAll();
            String[] headers = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
            for (String h : headers) {
                JLabel lbl = new JLabel(h, JLabel.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(h.equals("CN") ? Color.RED : new Color(50, 60, 70));
                grid.add(lbl);
            }

            YearMonth ym = YearMonth.of(currentYear[0], currentMonth[0]);
            LocalDate firstDay = ym.atDay(1);
            int startDayOfWeek = firstDay.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
            int lengthOfMonth = ym.lengthOfMonth();

            for (int i = 1; i < startDayOfWeek; i++) {
                grid.add(new JLabel(""));
            }

            for (int day = 1; day <= lengthOfMonth; day++) {
                final int selectedDay = day;
                JButton dayBtn = new JButton(String.valueOf(day));
                dayBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                dayBtn.setMargin(new Insets(2, 2, 2, 2));
                dayBtn.setFocusPainted(false);
                dayBtn.setBackground(day == currentDate.getDayOfMonth() &&
                        currentMonth[0] == currentDate.getMonthValue() &&
                        currentYear[0] == currentDate.getYear()
                        ? new Color(190, 227, 248) : Color.WHITE);

                dayBtn.addActionListener(ev -> {
                    LocalDate picked = LocalDate.of(currentYear[0], currentMonth[0], selectedDay);
                    setLocalDate(picked);
                    dialog.dispose();
                });
                grid.add(dayBtn);
            }

            grid.revalidate();
            grid.repaint();
        };

        cboMonth.addActionListener(e -> {
            currentMonth[0] = cboMonth.getSelectedIndex() + 1;
            renderDays.run();
        });

        cboYear.addActionListener(e -> {
            currentYear[0] = (int) cboYear.getSelectedItem();
            renderDays.run();
        });

        renderDays.run();
        dialog.add(grid, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        footer.setBackground(Color.WHITE);
        JButton btnToday = new JButton("Hôm nay");
        btnToday.addActionListener(e -> {
            setLocalDate(LocalDate.now());
            dialog.dispose();
        });
        footer.add(btnToday);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public LocalDate getLocalDate() {
        Date d = (Date) dateSpinner.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setLocalDate(LocalDate localDate) {
        if (localDate != null) {
            Date d = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            dateSpinner.setValue(d);
        }
    }

    public String getDateString() {
        return getLocalDate().toString();
    }

    public void setDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return;
        try {
            LocalDate ld = LocalDate.parse(dateStr.trim());
            setLocalDate(ld);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateSpinner.setEnabled(enabled);
        btnCalendar.setEnabled(enabled);
    }

    public JSpinner getDateSpinner() {
        return dateSpinner;
    }
}
