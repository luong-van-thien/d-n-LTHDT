import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ chính của ứng dụng MiniMart System (MainFrame).
 * Kiến trúc liên kết MVC toàn bộ các tầng.
 * Tích hợp Phân quyền Người dùng (RBAC) và Màn hình Đăng nhập / Đăng xuất.
 */
public class MainFrame extends JFrame {
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final InvoiceRepository invoiceRepo;
    private final UserRepository userRepo;

    private final ProductService productService;
    private final OrderService orderService;
    private final StatisticService statisticService;

    private ProductPanel productPanel;
    private PosPanel posPanel;
    private InvoiceHistoryPanel invoiceHistoryPanel;
    private StatisticPanel statisticPanel;

    private ProductController productController;
    private PosController posController;

    private JTabbedPane tabbedPane;
    private JLabel lblUserInfo;
    private User currentUser;

    public MainFrame() {
        this(new UserRepository(), null);
    }

    public MainFrame(UserRepository userRepository, User initialUser) {
        this.userRepo = userRepository != null ? userRepository : new UserRepository();
        this.currentUser = initialUser != null ? initialUser : this.userRepo.authenticate("admin", "admin123");

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
        if (this.currentUser != null) {
            applyUserRole(this.currentUser);
        }
    }

    private void initUI() {
        // Menu Bar
        setJMenuBar(createMenuBar());

        // Header Banner (Bao gồm Thông tin người dùng & Nút Đăng xuất)
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(new Color(24, 43, 73));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Bên trái Header: Tiêu đề hệ thống
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);

        JLabel lblLogo = new JLabel("HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Đồ án môn Lập trình hướng đối tượng (Java) - Học viện Công nghệ Bưu chính Viễn thông");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(new Color(200, 215, 235));

        pnlTitle.add(lblLogo);
        pnlTitle.add(lblSub);
        headerPanel.add(pnlTitle, BorderLayout.WEST);

        // Bên phải Header: Thông tin Tài khoản đang đăng nhập + Nút Đăng xuất
        JPanel pnlUserSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        pnlUserSection.setOpaque(false);

        lblUserInfo = new JLabel("👤 Đang đăng nhập: ...");
        lblUserInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserInfo.setForeground(new Color(254, 240, 138)); // Vàng nhạt nổi bật

        JButton btnSwitchUser = UIUtils.createStyledButton("Đổi tài khoản", new Color(59, 130, 246), Color.WHITE);
        btnSwitchUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSwitchUser.addActionListener(e -> handleLogout());

        JButton btnLogout = UIUtils.createStyledButton("Đăng xuất", new Color(220, 38, 38), Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.addActionListener(e -> handleLogout());

        pnlUserSection.add(lblUserInfo);
        pnlUserSection.add(btnSwitchUser);
        pnlUserSection.add(btnLogout);
        headerPanel.add(pnlUserSection, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main Tabbed Pane
        tabbedPane = new JTabbedPane();
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

        // Lắng nghe khi chuyển tab -> Kiểm tra quyền và tự động làm mới dữ liệu
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();

            // Kiểm tra phân quyền đa hình (Polymorphism check)
            if (currentUser != null && !currentUser.canAccessTab(index)) {
                JOptionPane.showMessageDialog(this,
                        String.format("Tài khoản [%s] không có quyền truy cập vào mục này!\nQuyền hạn của bạn: %s",
                                currentUser.getRole().getDisplayName(), currentUser.getRoleDescription()),
                        "Truy cập bị từ chối", JOptionPane.WARNING_MESSAGE);
                tabbedPane.setSelectedIndex(1); // Điều hướng về Tab POS
                return;
            }

            if (index == 0) productController.loadAllProducts();
            else if (index == 1) {
                posController.loadAvailableProducts();
                posController.loadCustomers();
            } else if (index == 2) invoiceHistoryPanel.loadInvoiceData();
            else if (index == 3) statisticPanel.refreshStatistics();
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Áp dụng Phân quyền Người dùng (RBAC):
     * Cập nhật Header, phân quyền các Tab và kích hoạt chế độ Thu ngân nếu cần.
     */
    public void applyUserRole(User user) {
        this.currentUser = user;
        if (user == null) return;

        // 1. Cập nhật Banner hiển thị người dùng
        lblUserInfo.setText(String.format("👤 %s  |  Vai trò: %s",
                user.getFullName(), user.getRole().getDisplayName()));

        // 2. Phân quyền các Tab theo phương thức đa hình canAccessTab
        boolean canAccessWarehouse = user.canAccessTab(0);
        boolean canAccessStats = user.canAccessTab(3);

        tabbedPane.setEnabledAt(0, canAccessWarehouse);
        tabbedPane.setEnabledAt(3, canAccessStats);

        tabbedPane.setTitleAt(0, canAccessWarehouse ? "  1. Quản Lý Kho & Sản Phẩm  " : "  1. Quản Lý Kho [ĐÃ KHÓA]  ");
        tabbedPane.setTitleAt(3, canAccessStats ? "  4. Thống Kê & Cảnh Báo  " : "  4. Thống Kê [ĐÃ KHÓA]  ");

        // Nếu là Thu ngân (không có quyền Tab 0), tự động điều hướng sang Tab 1 (POS)
        if (!canAccessWarehouse) {
            tabbedPane.setSelectedIndex(1);
        }

        // 3. Phân quyền Tab 1: Quản lý kho
        boolean canModify = user.canModifyProduct();
        productPanel.setCashierMode(!canModify);
    }

    /**
     * Xử lý Đăng xuất và chuyển đổi tài khoản.
     */
    private void handleLogout() {
        int opt = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất khỏi tài khoản hiện tại?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            setVisible(false);
            LoginDialog loginDialog = new LoginDialog(null, userRepo);
            loginDialog.setVisible(true);

            if (loginDialog.isSuccess() && loginDialog.getLoggedInUser() != null) {
                applyUserRole(loginDialog.getLoggedInUser());
                setVisible(true);
            } else {
                System.exit(0);
            }
        }
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

        JMenuItem itemSwitch = new JMenuItem("Đổi tài khoản đăng nhập");
        itemSwitch.addActionListener(e -> handleLogout());

        JMenuItem itemExit = new JMenuItem("Thoát chương trình");
        itemExit.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát ứng dụng?", "Thoát", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        menuSystem.add(itemReload);
        menuSystem.add(itemSwitch);
        menuSystem.addSeparator();
        menuSystem.add(itemExit);

        // Menu Trợ giúp
        JMenu menuHelp = new JMenu("Trợ giúp");
        menuHelp.setFont(UIUtils.BOLD_FONT);

        JMenuItem itemAbout = new JMenuItem("Thông tin đồ án & Nhóm tác giả");
        itemAbout.addActionListener(e -> {
            String msg = "ĐỒ ÁN GIỮA KỲ: HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)\n\n" +
                    "• Môn học: Lập trình hướng đối tượng (Java)\n" +
                    "• Giảng viên hướng dẫn: ThS. Nguyễn Thị Tuyết Hải - PTIT HCM\n" +
                    "• Nhóm sinh viên thực hiện:\n" +
                    "   1. Lương Văn Thiện - N24DCCN076 (Trưởng nhóm)\n" +
                    "   2. Trần Nhật Nguyên - N22DCCN057\n" +
                    "   3. Nguyễn Hải Phong - N24DCCN059\n" +
                    "   4. Ngô Đức Thành - N24DCCN074\n\n" +
                    "• Kiến trúc: MVC Pattern (Model - View - Controller)\n" +
                    "• Phân quyền người dùng (RBAC): Admin & Cashier\n" +
                    "• Áp dụng OOP: Encapsulation, Inheritance, Polymorphism, Abstract Class,\n" +
                    "  Interfaces, Lambda Expressions, Custom Exceptions, Collections, Swing GUI & File I/O.\n\n" +
                    "© 2026 PTIT HCM - All rights reserved.";
            JOptionPane.showMessageDialog(this, msg, "Giới thiệu đồ án", JOptionPane.INFORMATION_MESSAGE);
        });

        menuHelp.add(itemAbout);

        menuBar.add(menuSystem);
        menuBar.add(menuHelp);
        return menuBar;
    }
}
