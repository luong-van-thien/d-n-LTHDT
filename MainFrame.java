import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ chính của ứng dụng MiniMart System (MainFrame).
 * Kiến trúc liên kết MVC toàn bộ các tầng.
 */
public class MainFrame extends JFrame {
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final InvoiceRepository invoiceRepo;

    private final ProductService productService;
    private final OrderService orderService;
    private final StatisticService statisticService;

    private ProductPanel productPanel;
    private PosPanel posPanel;
    private InvoiceHistoryPanel invoiceHistoryPanel;
    private StatisticPanel statisticPanel;

    private ProductController productController;
    private PosController posController;

    public MainFrame() {
        // 1. Khởi tạo tầng Repository & Data
        productRepo = new ProductRepository();
        customerRepo = new CustomerRepository();
        invoiceRepo = new InvoiceRepository(customerRepo, productRepo);

        // 2. Khởi tạo tầng Service
        productService = new ProductService(productRepo);
        orderService = new OrderService(productRepo, customerRepo, invoiceRepo);
        statisticService = new StatisticService(productRepo, invoiceRepo);

        // 3. Cấu hình Frame
        setTitle("HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI - MINIMART SYSTEM [PTIT OOP]");
        setSize(1260, 780);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        // Menu Bar
        setJMenuBar(createMenuBar());

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 43, 73));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblLogo = new JLabel("HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Đồ án môn Lập trình hướng đối tượng (Java) - Học viện Công nghệ Bưu chính Viễn thông");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(new Color(200, 215, 235));

        headerPanel.add(lblLogo, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.BOLD_FONT);

        // Tạo các Panel
        productPanel = new ProductPanel();
        posPanel = new PosPanel();
        invoiceHistoryPanel = new InvoiceHistoryPanel(invoiceRepo);
        statisticPanel = new StatisticPanel(statisticService, productService);

        // Tạo Controllers
        productController = new ProductController(productPanel, productService);

        Runnable refreshAllData = () -> {
            productController.loadAllProducts();
            posController.loadAvailableProducts();
            posController.loadCustomers();
            invoiceHistoryPanel.loadInvoiceData();
            statisticPanel.refreshStatistics();
        };

        posController = new PosController(posPanel, productService, customerRepo, orderService, refreshAllData);

        // Thêm các tab (Dùng số thứ tự rõ ràng, không dùng emoji gây lỗi ô vuông)
        tabbedPane.addTab("  1. Quản Lý Kho & Sản Phẩm  ", productPanel);
        tabbedPane.addTab("  2. Quầy Thu Ngân (POS)  ", posPanel);
        tabbedPane.addTab("  3. Lịch Sử Hóa Đơn  ", invoiceHistoryPanel);
        tabbedPane.addTab("  4. Thống Kê & Cảnh Báo  ", statisticPanel);

        // Lắng nghe khi chuyển tab -> tự động làm mới dữ liệu tab đó
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index == 0) productController.loadAllProducts();
            else if (index == 1) {
                posController.loadAvailableProducts();
                posController.loadCustomers();
            } else if (index == 2) invoiceHistoryPanel.loadInvoiceData();
            else if (index == 3) statisticPanel.refreshStatistics();
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(UIUtils.BODY_FONT);

        // Menu Hệ thống
        JMenu menuSystem = new JMenu("Hệ thống");
        menuSystem.setFont(UIUtils.BOLD_FONT);

        JMenuItem itemReload = new JMenuItem("Tải lại toàn bộ dữ liệu từ File CSV");
        itemReload.addActionListener(e -> {
            productRepo.saveToFile();
            customerRepo.saveToFile();
            invoiceRepo.saveToFile();
            productController.loadAllProducts();
            posController.loadAvailableProducts();
            invoiceHistoryPanel.loadInvoiceData();
            statisticPanel.refreshStatistics();
            JOptionPane.showMessageDialog(this, "Đã đồng bộ lại toàn bộ dữ liệu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem itemExit = new JMenuItem("Thoát chương trình");
        itemExit.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát ứng dụng?", "Thoát", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        menuSystem.add(itemReload);
        menuSystem.addSeparator();
        menuSystem.add(itemExit);

        // Menu Trợ giúp
        JMenu menuHelp = new JMenu("Trợ giúp");
        menuHelp.setFont(UIUtils.BOLD_FONT);

        JMenuItem itemAbout = new JMenuItem("Thông tin đồ án & Nhóm tác giả");
        itemAbout.addActionListener(e -> {
            String msg = "ĐỒ ÁN GIỮA KỲ: HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)\n\n" +
                    "• Môn học: Lập trình hướng đối tượng (Java)\n" +
                    "• Giảng viên: ThS. Nguyễn Thị Tuyết Hải - PTIT HCM\n" +
                    "• Nhóm sinh viên thực hiện:\n" +
                    "   1. Lương Văn Thiện - N24DCCN076 (Trưởng nhóm)\n" +
                    "   2. Trần Nhật Nguyên - N22DCCN057\n" +
                    "   3. Nguyễn Hải Phong - N24DCCN059\n" +
                    "   4. Ngô Đức Thành - N24DCCN074\n\n" +
                    "• Kiến trúc: MVC Pattern (Model - View - Controller)\n" +
                    "• Áp dụng đầy đủ: Encapsulation, Inheritance, Polymorphism, Abstract Class,\n" +
                    "  Interfaces, Lambda Expressions, Exception Handling, Collections, Swing GUI & File I/O.\n\n" +
                    "© 2026 PTIT HCM - All rights reserved.";
            JOptionPane.showMessageDialog(this, msg, "Giới thiệu đồ án", JOptionPane.INFORMATION_MESSAGE);
        });

        menuHelp.add(itemAbout);

        menuBar.add(menuSystem);
        menuBar.add(menuHelp);
        return menuBar;
    }
}

