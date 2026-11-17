import javax.swing.*;
import java.awt.*;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Hộp thoại Xem trước Hóa đơn In nhiệt Siêu thị (ReceiptDialog):
 * - Mô phỏng mẫu hóa đơn in nhiệt cuộn giấy trắng chuẩn các chuỗi siêu thị Circle K / WinMart.
 * - Hiển thị logo, thông tin cửa hàng, bảng kê chi tiết từng sản phẩm, giá bán lịch sử.
 * - Mô phỏng mã vạch Barcode và mã vạch thanh toán điện tử.
 * - Hỗ trợ In trực tiếp (Printer Job) và Mở tệp văn bản sao lưu.
 */
public class ReceiptDialog extends JDialog {
    private final Invoice invoice;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ReceiptDialog(Window parent, Invoice invoice) {
        super(parent, "XEM TRƯỚC HÓA ĐƠN THANH TOÁN - " + invoice.getInvoiceId(), ModalityType.APPLICATION_MODAL);
        this.invoice = invoice;

        initComponents();
    }

    private void initComponents() {
        setSize(480, 720);
        setMinimumSize(new Dimension(440, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(0, 10));
        getContentPane().setBackground(UIUtils.BG_LIGHT);

        // Khung cuộn chứa phiếu hóa đơn
        JPanel receiptPaper = createReceiptPaper();
        JScrollPane scrollPane = new JScrollPane(receiptPaper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(UIUtils.BG_LIGHT);
        add(scrollPane, BorderLayout.CENTER);

        // Thanh nút bấm thao tác ở chân hộp thoại
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR));

        JButton btnPrint = UIUtils.createStyledButton("In hóa đơn", UIUtils.VectorIcons.receipt(16, Color.WHITE), UIUtils.PRIMARY_COLOR, Color.WHITE);
        JButton btnOpenTxt = UIUtils.createStyledButton("Mở file Text", UIUtils.VectorIcons.box(16, Color.WHITE), UIUtils.GRAY_COLOR, Color.WHITE);
        JButton btnClose = UIUtils.createStyledButton("Đóng", UIUtils.DANGER_COLOR, Color.WHITE);

        btnPrint.addActionListener(e -> handlePrint());
        btnOpenTxt.addActionListener(e -> handleOpenTextFile());
        btnClose.addActionListener(e -> dispose());

        bottomBar.add(btnOpenTxt);
        bottomBar.add(btnPrint);
        bottomBar.add(btnClose);
        add(bottomBar, BorderLayout.SOUTH);
    }

    /**
     * Tạo phiếu hóa đơn cuộn giấy nhiệt màu trắng, viền bóng đổ.
     */
    private JPanel createReceiptPaper() {
        JPanel paper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntialiasing(g2);

                int w = getWidth();
                int h = getHeight();

                // Bóng đổ nhẹ
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 3, w - 6, h - 4, 12, 12);

                // Thân giấy trắng
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w - 4, h - 4, 12, 12);

                // Viền xám mảnh
                g2.setColor(new Color(220, 226, 235));
                g2.drawRoundRect(0, 0, w - 5, h - 5, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        paper.setOpaque(false);
        paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));
        paper.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // 1. TIÊU ĐỀ THƯƠNG HIỆU SIÊU THỊ
        paper.add(createCenterLabel("MINIMART SYSTEM", new Font("Segoe UI", Font.BOLD, 18), UIUtils.NAVY_DARK));
        paper.add(createCenterLabel("CỬA HÀNG TIỆN LỢI & KHO HÀNG MINI - PTIT", new Font("Segoe UI", Font.BOLD, 12), UIUtils.PRIMARY_COLOR));
        paper.add(Box.createVerticalStrut(3));
        paper.add(createCenterLabel("Đ/c: 97 Man Thiện, P. Hiệp Phú, TP. Thủ Đức, TP.HCM", UIUtils.SMALL_FONT, UIUtils.SLATE_TEXT));
        paper.add(createCenterLabel("Hotline: 1900 1234 • Website: minimart.ptit.edu.vn", UIUtils.SMALL_FONT, UIUtils.SLATE_TEXT));
        paper.add(Box.createVerticalStrut(10));

        paper.add(createDashedSeparator());
        paper.add(Box.createVerticalStrut(8));

        // 2. THÔNG TIN HÓA ĐƠN
        paper.add(createCenterLabel("HÓA ĐƠN THANH TOÁN (RECEIPT)", new Font("Segoe UI", Font.BOLD, 14), UIUtils.NAVY_SLATE));
        paper.add(Box.createVerticalStrut(8));

        paper.add(createReceiptRow("Mã hóa đơn:", invoice.getInvoiceId(), true));
        paper.add(createReceiptRow("Thời gian:", invoice.getCreatedAt().format(TIME_FMT), false));

        String custName = invoice.getCustomer() != null ? invoice.getCustomer().getName() : "Khách lẻ / Vãng lai";
        paper.add(createReceiptRow("Khách hàng:", custName, false));

        String custType = invoice.getCustomer() != null ? invoice.getCustomer().getCustomerType() : "Khách thường";
        double discountPct = invoice.getCustomer() != null ? (invoice.getCustomer().getDiscountRate() * 100) : 0.0;
        String typeStr = String.format("%s (Giảm %.0f%%)", custType, discountPct);
        paper.add(createReceiptRow("Hạng thành viên:", typeStr, false));

        paper.add(Box.createVerticalStrut(8));
        paper.add(createDashedSeparator());
        paper.add(Box.createVerticalStrut(8));

        // 3. TIÊU ĐỀ BẢNG MÓN HÀNG
        JPanel itemHeader = new JPanel(new BorderLayout(4, 0));
        itemHeader.setOpaque(false);
        JLabel lblColName = new JLabel("Mặt hàng");
        lblColName.setFont(UIUtils.SMALL_BOLD);
        lblColName.setForeground(UIUtils.NAVY_SLATE);

        JLabel lblColPrice = new JLabel("SL x Đơn giá    Thành tiền");
        lblColPrice.setFont(UIUtils.SMALL_BOLD);
        lblColPrice.setForeground(UIUtils.NAVY_SLATE);

        itemHeader.add(lblColName, BorderLayout.WEST);
        itemHeader.add(lblColPrice, BorderLayout.EAST);
        paper.add(itemHeader);
        paper.add(Box.createVerticalStrut(4));

        // DANH SÁCH MÓN HÀNG
        int stt = 1;
        for (InvoiceItem item : invoice.getItems()) {
            JPanel itemRow = new JPanel(new BorderLayout(4, 0));
            itemRow.setOpaque(false);

            String nameText = String.format("%d. %s", stt++, item.getProduct().getName());
            if (nameText.length() > 22) {
                nameText = nameText.substring(0, 20) + "..";
            }
            JLabel lblItemName = new JLabel(nameText);
            lblItemName.setFont(UIUtils.BODY_FONT);
            lblItemName.setForeground(UIUtils.NAVY_SLATE);

            String priceText = String.format("%d x %s = %s",
                    item.getQuantity(),
                    UIUtils.formatCurrency(item.getUnitPrice()),
                    UIUtils.formatCurrency(item.getSubTotal()));
            JLabel lblItemPrice = new JLabel(priceText);
            lblItemPrice.setFont(UIUtils.BODY_FONT);
            lblItemPrice.setForeground(UIUtils.NAVY_SLATE);

            itemRow.add(lblItemName, BorderLayout.WEST);
            itemRow.add(lblItemPrice, BorderLayout.EAST);
            paper.add(itemRow);
            paper.add(Box.createVerticalStrut(3));
        }

        paper.add(Box.createVerticalStrut(6));
        paper.add(createDashedSeparator());
        paper.add(Box.createVerticalStrut(8));

        // 4. TỔNG KẾT TÀI CHÍNH
        paper.add(createReceiptRow("Tổng tiền hàng:", UIUtils.formatCurrency(invoice.getTotalAmount()), false));
        if (invoice.getDiscountAmount() > 0) {
            paper.add(createReceiptRow("Chiết khấu thành viên:", "- " + UIUtils.formatCurrency(invoice.getDiscountAmount()), false));
        }

        paper.add(Box.createVerticalStrut(4));
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel lblTotalText = new JLabel("TỔNG THANH TOÁN:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalText.setForeground(UIUtils.NAVY_DARK);

        JLabel lblTotalVal = new JLabel(UIUtils.formatCurrency(invoice.getFinalAmount()));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalVal.setForeground(UIUtils.SUCCESS_COLOR);

        totalRow.add(lblTotalText, BorderLayout.WEST);
        totalRow.add(lblTotalVal, BorderLayout.EAST);
        paper.add(totalRow);
        paper.add(Box.createVerticalStrut(6));

        paper.add(createReceiptRow("Tiền khách đưa:", UIUtils.formatCurrency(invoice.getCustomerCash()), false));
        paper.add(createReceiptRow("Tiền thối lại:", UIUtils.formatCurrency(invoice.getReturnCash()), true));

        paper.add(Box.createVerticalStrut(10));
        paper.add(createDashedSeparator());
        paper.add(Box.createVerticalStrut(10));

        // 5. MÔ PHỎNG MÃ VẠCH BARCODE BẰNG GRAPHICS2D
        JPanel barcodePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = 42;
                int startX = (w - 240) / 2;

                g2.setColor(Color.BLACK);
                // Vẽ các vạch barcode mô phỏng
                int[] barWidths = {2, 1, 3, 1, 2, 4, 1, 2, 3, 1, 2, 1, 4, 2, 1, 3, 2, 1, 2, 4, 1, 3, 1, 2, 3, 1, 4, 2, 1, 2, 3, 1, 2, 4, 1, 2, 3, 1};
                int curX = startX;
                for (int i = 0; i < barWidths.length; i++) {
                    if (i % 2 == 0) {
                        g2.fillRect(curX, 0, barWidths[i], h);
                    }
                    curX += barWidths[i] + 2;
                }

                g2.setFont(new Font("Consolas", Font.PLAIN, 11));
                FontMetrics fm = g2.getFontMetrics();
                String codeStr = "*" + invoice.getInvoiceId() + "*";
                int strW = fm.stringWidth(codeStr);
                g2.drawString(codeStr, (w - strW) / 2, h + 14);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        barcodePanel.setOpaque(false);
        barcodePanel.setPreferredSize(new Dimension(320, 60));
        barcodePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 60));
        paper.add(barcodePanel);

        // 6. LỜI CẢM ƠN
        paper.add(Box.createVerticalStrut(8));
        paper.add(createCenterLabel("CẢM ƠN QUÝ KHÁCH & HẸN GẶP LẠI!", new Font("Segoe UI", Font.BOLD, 12), UIUtils.NAVY_SLATE));
        paper.add(createCenterLabel("Hóa đơn điện tử có giá trị đổi trả trong 07 ngày", UIUtils.SMALL_FONT, UIUtils.SLATE_MUTED));

        return paper;
    }

    private JLabel createCenterLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JPanel createReceiptRow(String title, String val, boolean boldVal) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIUtils.BODY_FONT);
        lblTitle.setForeground(UIUtils.SLATE_TEXT);

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(boldVal ? UIUtils.BOLD_FONT : UIUtils.BODY_FONT);
        lblVal.setForeground(UIUtils.NAVY_SLATE);

        row.add(lblTitle, BorderLayout.WEST);
        row.add(lblVal, BorderLayout.EAST);
        return row;
    }

    private JComponent createDashedSeparator() {
        return new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(203, 213, 225));
                Stroke dashed = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 3}, 0);
                g2.setStroke(dashed);
                g2.drawLine(0, 4, getWidth(), 4);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 8);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Short.MAX_VALUE, 8);
            }
        };
    }

    private void handlePrint() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("HoaDon_" + invoice.getInvoiceId());

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            getContentPane().printAll(g2);
            return Printable.PAGE_EXISTS;
        });

        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Đã gửi lệnh in hóa đơn thành công!", "Thông báo in", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi in ấn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleOpenTextFile() {
        try {
            File file = new File(invoice.getInvoiceId() + ".txt");
            if (!file.exists()) {
                invoice.exportToFile(file.getAbsolutePath());
            }
            if (file.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(this, "Tệp hóa đơn được lưu tại: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi mở tệp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
