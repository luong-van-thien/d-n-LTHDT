import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Giao diện Xem Lịch Sử Hóa Đơn & Chi Tiết Từng Hóa Đơn (InvoiceHistoryPanel) chuẩn Enterprise:
 * - Thiết kế Master - Detail trong 2 Card Panels bo góc mềm mại.
 * - Toolbar tìm kiếm đa năng theo mã HĐ hoặc tên khách hàng.
 * - Bảng hiển thị thông tin thanh toán rõ ràng, hỗ trợ mở hóa đơn văn bản và xuất CSV.
 */
public class InvoiceHistoryPanel extends JPanel {
    private final InvoiceRepository invoiceRepo;

    // Master Table: Hóa đơn
    private JTable tblInvoices;
    private DefaultTableModel modelInvoices;
    private JTextField txtSearch;
    private JButton btnReload;
    private JButton btnOpenReceiptFile;
    private JButton btnExportCsv;

    // Detail Table: Chi tiết món hàng của hóa đơn đang chọn
    private JTable tblDetails;
    private DefaultTableModel modelDetails;
    private JLabel lblDetailSubtitle;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public InvoiceHistoryPanel(InvoiceRepository invoiceRepo) {
        this.invoiceRepo = invoiceRepo;

        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        initComponents();
        loadInvoiceData();
    }

    private void initComponents() {
        // =========================================================================
        // 1. TOP PANEL: TOOLBAR TÌM KIẾM & THAO TÁC (CARD PANEL)
        // =========================================================================
        JPanel topPanel = UIUtils.createCardPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel lblSearch = new JLabel("Tìm mã HĐ / Tên KH:");
        lblSearch.setFont(UIUtils.BOLD_FONT);
        lblSearch.setForeground(UIUtils.NAVY_SLATE);
        topPanel.add(lblSearch);

        txtSearch = new JTextField(18);
        UIUtils.styleTextField(txtSearch);
        txtSearch.setToolTipText("Gõ mã hóa đơn hoặc tên khách hàng để lọc...");
        topPanel.add(txtSearch);

        JButton btnSearch = UIUtils.createStyledButton("Tìm kiếm", UIUtils.VectorIcons.search(15, Color.WHITE), UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnReload = UIUtils.createStyledButton("Tải lại", UIUtils.VectorIcons.refresh(15, Color.WHITE), UIUtils.GRAY_COLOR, Color.WHITE);
        btnOpenReceiptFile = UIUtils.createStyledButton("Mở tệp hóa đơn in", UIUtils.VectorIcons.receipt(15, Color.WHITE), UIUtils.SUCCESS_COLOR, Color.WHITE);
        btnExportCsv = UIUtils.createStyledButton("Xuất file báo cáo", UIUtils.VectorIcons.box(15, Color.WHITE), new Color(13, 110, 253), Color.WHITE);

        topPanel.add(btnSearch);
        topPanel.add(btnReload);
        topPanel.add(btnOpenReceiptFile);
        topPanel.add(btnExportCsv);

        add(topPanel, BorderLayout.NORTH);

        // =========================================================================
        // 2. CENTER PANEL: 2 BẢNG MASTER - DETAIL TRONG CARD TIÊU CHUẨN
        // =========================================================================
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        centerPanel.setOpaque(false);

        // --- 2A. MASTER CARD: DANH SÁCH HÓA ĐƠN ---
        JPanel masterCard = UIUtils.createCardPanel(new BorderLayout(0, 8));
        masterCard.add(UIUtils.createSectionHeader("DANH SÁCH HÓA ĐƠN ĐÃ GIAO DỊCH", "Lịch sử mua bán và thu chi tiền mặt chi tiết tại quầy thu ngân", UIUtils.VectorIcons.receipt(18, UIUtils.PRIMARY_COLOR)), BorderLayout.NORTH);

        String[] colsMaster = {"Mã Hóa Đơn", "Thời gian lập", "Khách hàng", "Tổng tiền hàng", "Giảm giá", "Thanh toán", "Tiền khách đưa", "Tiền thối lại"};
        modelInvoices = new DefaultTableModel(colsMaster, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblInvoices = UIUtils.createCustomTable(modelInvoices);
        tblInvoices.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblInvoices.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblInvoices.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblInvoices.getColumnModel().getColumn(3).setPreferredWidth(95);
        tblInvoices.getColumnModel().getColumn(4).setPreferredWidth(85);
        tblInvoices.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblInvoices.getColumnModel().getColumn(6).setPreferredWidth(95);
        tblInvoices.getColumnModel().getColumn(7).setPreferredWidth(95);

        tblInvoices.getColumnModel().getColumn(0).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.CENTER));
        tblInvoices.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.CENTER));
        tblInvoices.getColumnModel().getColumn(2).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));

        for (int i = 3; i <= 7; i++) {
            tblInvoices.getColumnModel().getColumn(i).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        }

        JScrollPane scrollMaster = new JScrollPane(tblInvoices);
        scrollMaster.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        UIUtils.installSmartDeselection(tblInvoices, scrollMaster);
        masterCard.add(scrollMaster, BorderLayout.CENTER);
        centerPanel.add(masterCard);

        // --- 2B. DETAIL CARD: CHI TIẾT MÓN HÀNG ---
        JPanel detailCard = UIUtils.createCardPanel(new BorderLayout(0, 8));

        JPanel detailHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        detailHeader.setOpaque(false);
        JLabel lblDetailTitle = new JLabel("CHI TIẾT CÁC MẶT HÀNG TRONG HÓA ĐƠN");
        lblDetailTitle.setFont(UIUtils.HEADER_FONT);
        lblDetailTitle.setForeground(UIUtils.NAVY_SLATE);
        lblDetailTitle.setIcon(UIUtils.VectorIcons.box(18, UIUtils.SUCCESS_COLOR));
        lblDetailTitle.setIconTextGap(8);

        lblDetailSubtitle = new JLabel("Nhấp chọn một hóa đơn ở danh sách phía trên để xem chi tiết từng món hàng.");
        lblDetailSubtitle.setFont(UIUtils.SMALL_FONT);
        lblDetailSubtitle.setForeground(UIUtils.SLATE_TEXT);

        detailHeader.add(lblDetailTitle);
        detailHeader.add(lblDetailSubtitle);
        detailCard.add(detailHeader, BorderLayout.NORTH);

        String[] colsDetail = {"Mã SP", "Tên sản phẩm", "Danh mục", "Đơn giá", "Số lượng mua", "Thành tiền"};
        modelDetails = new DefaultTableModel(colsDetail, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblDetails = UIUtils.createCustomTable(modelDetails);
        tblDetails.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblDetails.getColumnModel().getColumn(1).setPreferredWidth(210);
        tblDetails.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblDetails.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblDetails.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblDetails.getColumnModel().getColumn(5).setPreferredWidth(100);

        tblDetails.getColumnModel().getColumn(0).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.CENTER));
        tblDetails.getColumnModel().getColumn(1).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.LEFT));
        tblDetails.getColumnModel().getColumn(2).setCellRenderer(new UIUtils.BadgeCellRenderer());
        tblDetails.getColumnModel().getColumn(3).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblDetails.getColumnModel().getColumn(4).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));
        tblDetails.getColumnModel().getColumn(5).setCellRenderer(UIUtils.createToolTipRenderer(JLabel.RIGHT));

        JScrollPane scrollDetails = new JScrollPane(tblDetails);
        scrollDetails.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        UIUtils.installSmartDeselection(tblDetails, scrollDetails);
        detailCard.add(scrollDetails, BorderLayout.CENTER);
        centerPanel.add(detailCard);

        add(centerPanel, BorderLayout.CENTER);

        // LISTENERS
        btnSearch.addActionListener(e -> filterInvoices());
        txtSearch.addActionListener(e -> filterInvoices());
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filterInvoices(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filterInvoices(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filterInvoices(); }
        });
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            loadInvoiceData();
        });

        // Sự kiện chọn dòng ở bảng hóa đơn -> hiển thị chi tiết
        tblInvoices.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedInvoiceDetails();
            }
        });

        btnOpenReceiptFile.addActionListener(e -> openReceiptTextFile());
        btnExportCsv.addActionListener(e -> exportInvoicesToCsv());
    }

    public void loadInvoiceData() {
        List<Invoice> list = invoiceRepo.getAll();
        updateTable(list);
    }

    private void filterInvoices() {
        String query = txtSearch.getText().trim();
        List<Invoice> filtered = invoiceRepo.getAll().stream()
                .filter(inv -> {
                    boolean matchId = UIUtils.containsIgnoreCaseAndAccent(inv.getInvoiceId(), query);
                    boolean matchCust = inv.getCustomer() != null && UIUtils.containsIgnoreCaseAndAccent(inv.getCustomer().getName(), query);
                    return query.isEmpty() || matchId || matchCust;
                })
                .toList();
        updateTable(filtered);
    }

    private void updateTable(List<Invoice> invoices) {
        modelInvoices.setRowCount(0);
        modelDetails.setRowCount(0);
        lblDetailSubtitle.setText("Nhấp chọn một hóa đơn ở danh sách phía trên để xem chi tiết từng món hàng.");

        for (Invoice inv : invoices) {
            String custName = inv.getCustomer() != null ? inv.getCustomer().getName() : "Khách vãng lai";
            modelInvoices.addRow(new Object[]{
                    inv.getInvoiceId(),
                    inv.getCreatedAt().format(TIME_FMT),
                    custName,
                    UIUtils.formatCurrency(inv.getTotalAmount()),
                    UIUtils.formatCurrency(inv.getDiscountAmount()),
                    UIUtils.formatCurrency(inv.getFinalAmount()),
                    UIUtils.formatCurrency(inv.getCustomerCash()),
                    UIUtils.formatCurrency(inv.getReturnCash())
            });
        }
    }

    private void showSelectedInvoiceDetails() {
        int row = tblInvoices.getSelectedRow();
        if (row < 0) {
            modelDetails.setRowCount(0);
            return;
        }

        String invoiceId = (String) tblInvoices.getValueAt(row, 0);
        Invoice invoice = invoiceRepo.findById(invoiceId);
        if (invoice == null) return;

        lblDetailSubtitle.setText(String.format("Đang xem chi tiết đơn hàng [%s] • Khách hàng: %s • Tổng thanh toán: %s",
                invoice.getInvoiceId(),
                invoice.getCustomer() != null ? invoice.getCustomer().getName() : "Khách vãng lai",
                UIUtils.formatCurrency(invoice.getFinalAmount())));

        modelDetails.setRowCount(0);
        for (InvoiceItem item : invoice.getItems()) {
            modelDetails.addRow(new Object[]{
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getProduct().getCategory().getDisplayName(),
                    UIUtils.formatCurrency(item.getUnitPrice()),
                    item.getQuantity(),
                    UIUtils.formatCurrency(item.getSubTotal())
            });
        }
    }

    private void openReceiptTextFile() {
        int row = tblInvoices.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hóa đơn trong bảng để mở tệp in!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String invoiceId = (String) tblInvoices.getValueAt(row, 0);
        Invoice invoice = invoiceRepo.findById(invoiceId);
        if (invoice == null) return;

        File receiptDir = new File("receipts");
        if (!receiptDir.exists()) receiptDir.mkdirs();
        File receiptFile = new File(receiptDir, "hoadon_" + invoiceId + ".txt");

        if (!receiptFile.exists()) {
            try {
                invoice.exportToFile(receiptFile.getAbsolutePath());
            } catch (Exception ignored) {}
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN) && receiptFile.exists()) {
                Desktop.getDesktop().open(receiptFile);
            } else {
                JOptionPane.showMessageDialog(this, "Đường dẫn tệp hóa đơn:\n" + receiptFile.getAbsolutePath(),
                        "Thông tin hóa đơn", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở tệp hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportInvoicesToCsv() {
        List<Invoice> list = invoiceRepo.getAll();
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có hóa đơn nào để xuất file!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = new File("BaoCao_LichSu_HoaDon.csv");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write('\ufeff'); // UTF-8 BOM
            bw.write("Mã Hóa Đơn,Thời Gian Lập,Tên Khách Hàng,Tổng Tiền Hàng,Giảm Giá,Thanh Toán Thực Tế,Tiền Khách Đưa,Tiền Thừa\n");
            for (Invoice inv : list) {
                String cust = inv.getCustomer() != null ? inv.getCustomer().getName() : "Khách vãng lai";
                bw.write(String.format("\"%s\",\"%s\",\"%s\",%.0f,%.0f,%.0f,%.0f,%.0f\n",
                        inv.getInvoiceId(),
                        inv.getCreatedAt().format(TIME_FMT),
                        cust,
                        inv.getTotalAmount(),
                        inv.getDiscountAmount(),
                        inv.getFinalAmount(),
                        inv.getCustomerCash(),
                        inv.getReturnCash()));
            }

            int choice = JOptionPane.showConfirmDialog(this,
                    "Đã xuất thành công tệp lịch sử: " + file.getAbsolutePath() + "\nBạn có muốn mở tệp ngay không?",
                    "Xuất File Thành Công", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất tệp CSV: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
