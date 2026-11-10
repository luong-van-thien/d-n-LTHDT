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
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Giao diện Xem Lịch Sử Hóa Đơn & Chi Tiết Từng Hóa Đơn (InvoiceHistoryPanel).
 * Thiết kế giao diện Master - Detail chuẩn nghiệp vụ kế toán & thu ngân.
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
    private JLabel lblDetailTitle;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public InvoiceHistoryPanel(InvoiceRepository invoiceRepo) {
        this.invoiceRepo = invoiceRepo;

        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadInvoiceData();
    }

    private void initComponents() {
        // TOP: Toolbar tìm kiếm & chức năng
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        topPanel.add(new JLabel("Tìm mã HĐ / Tên KH:"));
        txtSearch = new JTextField(18);
        topPanel.add(txtSearch);

        JButton btnSearch = UIUtils.createStyledButton("Tìm kiếm", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnReload = UIUtils.createStyledButton("Tải lại", UIUtils.GRAY_COLOR, Color.WHITE);
        btnOpenReceiptFile = UIUtils.createStyledButton("Mở File In (.txt)", UIUtils.SUCCESS_COLOR, Color.WHITE);
        btnExportCsv = UIUtils.createStyledButton("Xuất Excel/CSV", new Color(13, 110, 253), Color.WHITE);

        topPanel.add(btnSearch);
        topPanel.add(btnReload);
        topPanel.add(btnOpenReceiptFile);
        topPanel.add(btnExportCsv);

        add(topPanel, BorderLayout.NORTH);

        // CENTER: 2 bảng Master - Detail
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        centerPanel.setBackground(UIUtils.BG_LIGHT);

        // 1. MASTER TABLE: Danh sách hóa đơn
        String[] colsMaster = {"Mã Hóa Đơn", "Thời gian lập", "Khách hàng", "Tổng tiền hàng", "Giảm giá", "Thanh toán", "Tiền khách đưa", "Tiền thối lại"};
        modelInvoices = new DefaultTableModel(colsMaster, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblInvoices = new JTable(modelInvoices);
        tblInvoices.setFont(UIUtils.BODY_FONT);
        tblInvoices.setRowHeight(26);
        tblInvoices.getTableHeader().setFont(UIUtils.BOLD_FONT);
        tblInvoices.getTableHeader().setReorderingAllowed(false);
        tblInvoices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblInvoices.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblInvoices.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblInvoices.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblInvoices.getColumnModel().getColumn(3).setPreferredWidth(95);
        tblInvoices.getColumnModel().getColumn(4).setPreferredWidth(85);
        tblInvoices.getColumnModel().getColumn(5).setPreferredWidth(95);
        tblInvoices.getColumnModel().getColumn(6).setPreferredWidth(95);
        tblInvoices.getColumnModel().getColumn(7).setPreferredWidth(95);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        for (int i = 3; i <= 7; i++) {
            tblInvoices.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }

        JScrollPane scrollMaster = new JScrollPane(tblInvoices);
        scrollMaster.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "DANH SÁCH HÓA ĐƠN ĐÃ GIAO DỊCH", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));
        centerPanel.add(scrollMaster);

        // 2. DETAIL TABLE: Chi tiết các mặt hàng trong hóa đơn
        JPanel detailContainer = new JPanel(new BorderLayout(5, 5));
        detailContainer.setBackground(Color.WHITE);
        detailContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "CHI TIẾT CÁC MẶT HÀNG TRONG HÓA ĐƠN", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));

        lblDetailTitle = new JLabel("Nhấp chọn 1 hóa đơn ở bảng trên để xem chi tiết các mặt hàng đã mua.", JLabel.LEFT);
        lblDetailTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblDetailTitle.setForeground(new Color(100, 110, 120));
        lblDetailTitle.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        detailContainer.add(lblDetailTitle, BorderLayout.NORTH);

        String[] colsDetail = {"Mã SP", "Tên sản phẩm", "Danh mục", "Đơn giá", "Số lượng mua", "Thành tiền"};
        modelDetails = new DefaultTableModel(colsDetail, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblDetails = new JTable(modelDetails);
        tblDetails.setFont(UIUtils.BODY_FONT);
        tblDetails.setRowHeight(24);
        tblDetails.getTableHeader().setFont(UIUtils.BOLD_FONT);
        tblDetails.getTableHeader().setReorderingAllowed(false);

        tblDetails.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblDetails.getColumnModel().getColumn(1).setPreferredWidth(210);
        tblDetails.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblDetails.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblDetails.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblDetails.getColumnModel().getColumn(5).setPreferredWidth(100);

        tblDetails.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblDetails.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tblDetails.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        detailContainer.add(new JScrollPane(tblDetails), BorderLayout.CENTER);
        centerPanel.add(detailContainer);

        add(centerPanel, BorderLayout.CENTER);

        // LISTENERS
        btnSearch.addActionListener(e -> filterInvoices());
        txtSearch.addActionListener(e -> filterInvoices());
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
        String query = txtSearch.getText().trim().toLowerCase();
        List<Invoice> filtered = invoiceRepo.getAll().stream()
                .filter(inv -> {
                    boolean matchId = inv.getInvoiceId().toLowerCase().contains(query);
                    boolean matchCust = inv.getCustomer() != null && inv.getCustomer().getName().toLowerCase().contains(query);
                    return query.isEmpty() || matchId || matchCust;
                })
                .toList();
        updateTable(filtered);
    }

    private void updateTable(List<Invoice> invoices) {
        modelInvoices.setRowCount(0);
        modelDetails.setRowCount(0);
        lblDetailTitle.setText("Nhấp chọn 1 hóa đơn ở bảng trên để xem chi tiết các mặt hàng đã mua.");

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

        lblDetailTitle.setText(String.format("Chi tiết đơn hàng [%s] - Khách hàng: %s - Tổng thanh toán: %s",
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hóa đơn trong danh sách trước!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tblInvoices.getValueAt(row, 0);
        File receiptFile = new File(id + ".txt");
        if (!receiptFile.exists()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy file in: " + receiptFile.getName(), "Thông báo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(receiptFile);
            } else {
                JOptionPane.showMessageDialog(this, "Vị trí file: " + receiptFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi mở file: " + ex.getMessage());
        }
    }

    private void exportInvoicesToCsv() {
        List<Invoice> list = invoiceRepo.getAll();
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có hóa đơn nào để xuất dữ liệu!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File exportFile = new File("BaoCao_LichSu_HoaDon.csv");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(exportFile), StandardCharsets.UTF_8))) {
            writer.write("\uFEFF"); // UTF-8 BOM để Excel hiển thị đúng dấu tiếng Việt
            writer.write("Mã Hóa Đơn,Thời Gian,Khách Hàng,Loại KH,Tổng Tiền Hàng,Chiết Khấu,Cần Thanh Toán,Tiền Khách Đưa,Tiền Thối Lại\n");
            for (Invoice inv : list) {
                String custName = inv.getCustomer() != null ? inv.getCustomer().getName() : "Khách vãng lai";
                String custType = inv.getCustomer() != null ? inv.getCustomer().getCustomerType() : "Khách lẻ";
                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%.0f,%.0f,%.0f,%.0f,%.0f\n",
                        inv.getInvoiceId(),
                        inv.getCreatedAt().format(TIME_FMT),
                        custName,
                        custType,
                        inv.getTotalAmount(),
                        inv.getDiscountAmount(),
                        inv.getFinalAmount(),
                        inv.getCustomerCash(),
                        inv.getReturnCash()));
            }
            JOptionPane.showMessageDialog(this, "Đã xuất báo cáo hóa đơn thành công ra tệp:\n" + exportFile.getAbsolutePath(),
                    "Xuất file thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
