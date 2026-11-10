import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Giao diện Xem Lịch Sử Hóa Đơn & Mở File In (InvoiceHistoryPanel).
 */
public class InvoiceHistoryPanel extends JPanel {
    private final InvoiceRepository invoiceRepo;
    private JTable tblInvoices;
    private DefaultTableModel modelInvoices;
    private JTextField txtSearch;
    private JButton btnReload;
    private JButton btnOpenReceiptFile;

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
        // TOP: Toolbar
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
        btnOpenReceiptFile = UIUtils.createStyledButton("Mở File Hóa Đơn In (.txt)", UIUtils.SUCCESS_COLOR, Color.WHITE);

        topPanel.add(btnSearch);
        topPanel.add(btnReload);
        topPanel.add(btnOpenReceiptFile);

        add(topPanel, BorderLayout.NORTH);

        // CENTER: JTable
        String[] cols = {"Mã Hóa Đơn", "Thời gian lập", "Khách hàng", "Tổng tiền hàng", "Giảm giá", "Thanh toán", "Tiền khách đưa", "Tiền thối lại"};
        modelInvoices = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblInvoices = new JTable(modelInvoices);
        tblInvoices.setFont(UIUtils.BODY_FONT);
        tblInvoices.setRowHeight(26);
        tblInvoices.getTableHeader().setFont(UIUtils.BOLD_FONT);
        tblInvoices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        for (int i = 3; i <= 7; i++) {
            tblInvoices.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tblInvoices);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1),
                "DANH SÁCH CÁC HÓA ĐƠN ĐÃ GIAO DỊCH", TitledBorder.LEFT, TitledBorder.TOP, UIUtils.HEADER_FONT, UIUtils.PRIMARY_COLOR
        ));
        add(scrollPane, BorderLayout.CENTER);

        // LISTENERS
        btnSearch.addActionListener(e -> filterInvoices());
        txtSearch.addActionListener(e -> filterInvoices());
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            loadInvoiceData();
        });

        btnOpenReceiptFile.addActionListener(e -> {
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
        });
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
        for (Invoice inv : invoices) {
            String custName = inv.getCustomer() != null ? inv.getCustomer().getName() : "Khách vãng lai";
            modelInvoices.addRow(new Object[]{
                    inv.getInvoiceId(),
                    inv.getCreatedAt() != null ? inv.getCreatedAt().format(TIME_FMT) : "",
                    custName,
                    UIUtils.formatCurrency(inv.getTotalAmount()),
                    UIUtils.formatCurrency(inv.getDiscountAmount()),
                    UIUtils.formatCurrency(inv.getFinalAmount()),
                    UIUtils.formatCurrency(inv.getCustomerCash()),
                    UIUtils.formatCurrency(inv.getReturnCash())
            });
        }
    }
}

