import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ chính của ứng dụng MiniMart System (MainFrame) chuẩn phong cách Enterprise SaaS:
 * - Thanh Navbar trên cùng phối màu Navy Slate chuyển sắc đẳng cấp.
 * - Thẻ định danh người dùng (User Chip) kèm Huy hiệu chức vụ (Role Badge) trực quan.
 * - Hệ thống Tab phẳng hiện đại (Modern Flat TabbedPane) kèm Vector Icons sắc nét.
 * - Tích hợp đầy đủ kiến trúc MVC, kiểm soát phân quyền đa hình (RBAC).
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
    private JLabel lblUserName;
    private JLabel lblUserRoleBadge;
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
        setSize(1300, 800);
        setMinimumSize(new Dimension(1050, 680));
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

        // =========================================================================
        // 1. HEADER NAVBAR HIỆN ĐẠI (DARK SLATE GRADIENT)
        // =========================================================================
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntialiasing(g2);

                GradientPaint gp = new GradientPaint(0, 0, UIUtils.NAVY_DARK, getWidth(), getHeight(), UIUtils.NAVY_SLATE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Đường viền màu xanh rực rỡ ở chân Navbar
                g2.setColor(UIUtils.PRIMARY_COLOR);
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        headerPanel.setLayout(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        // Bên trái Header: Thương hiệu hệ thống
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);

        JLabel lblLogo = new JLabel("HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setIcon(UIUtils.VectorIcons.cart(22, new Color(96, 165, 250)));
        lblLogo.setIconTextGap(10);

        JLabel lblSub = new JLabel("Đồ án môn Lập trình hướng đối tượng (Java) - Học viện Công nghệ Bưu chính Viễn thông");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(186, 203, 226));

        pnlTitle.add(lblLogo);
        pnlTitle.add(lblSub);
        headerPanel.add(pnlTitle, BorderLayout.WEST);

        // Bên phải Header: Thẻ hồ sơ người dùng (User Profile Chip) & Nút Đăng xuất
        JPanel pnlUserSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        pnlUserSection.setOpaque(false);

        // User Chip Panel bo tròn
        JPanel userChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntialiasing(g2);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        userChip.setOpaque(false);
        userChip.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 12));

        JLabel lblAvatar = new JLabel(UIUtils.VectorIcons.user(18, new Color(147, 197, 253)));
        lblUserName = new JLabel("Đang tải...");
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserName.setForeground(Color.WHITE);

        lblUserRoleBadge = new JLabel(" Admin ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntialiasing(g2);
                g2.setColor(new Color(37, 99, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblUserRoleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUserRoleBadge.setForeground(Color.WHITE);
        lblUserRoleBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        userChip.add(lblAvatar);
        userChip.add(lblUserName);
        userChip.add(lblUserRoleBadge);

        JButton btnLogout = UIUtils.createStyledButton("Đăng xuất", UIUtils.DANGER_COLOR, Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setToolTipText("Đăng xuất khỏi tài khoản hiện tại để đăng nhập tài khoản khác");
        btnLogout.addActionListener(e -> handleLogout());

        pnlUserSection.add(userChip);
        pnlUserSection.add(btnLogout);
        headerPanel.add(pnlUserSection, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // =========================================================================
        // 2. MAIN TABBED PANE VỚI GIAO DIỆN PHẲNG (MODERN FLAT TABS)
        // =========================================================================
        tabbedPane = new JTabbedPane();
        UIUtils.applyModernTabbedPaneUI(tabbedPane);

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

        // Thêm 4 Tab chính kèm Vector Icons sắc nét
        tabbedPane.addTab("  1. Quản Lý Kho & Sản Phẩm  ", UIUtils.VectorIcons.box(16, UIUtils.PRIMARY_COLOR), productPanel);
        tabbedPane.addTab("  2. Quầy Bán Hàng & POS  ", UIUtils.VectorIcons.cart(16, UIUtils.SUCCESS_COLOR), posPanel);
        tabbedPane.addTab("  3. Lịch Sử Hóa Đơn  ", UIUtils.VectorIcons.receipt(16, UIUtils.WARNING_COLOR), invoiceHistoryPanel);
        tabbedPane.addTab("  4. Thống Kê & Cảnh Báo  ", UIUtils.VectorIcons.chart(16, UIUtils.PURPLE_COLOR), statisticPanel);

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

        // 1. Cập nhật User Chip trên Navbar
        lblUserName.setText(user.getFullName());
        lblUserRoleBadge.setText(" " + user.getRole().getDisplayName() + " ");

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
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_COLOR));

        // Menu Hệ thống
        JMenu menuSystem = new JMenu("Hệ thống");
        menuSystem.setFont(UIUtils.BOLD_FONT);
        menuSystem.setForeground(UIUtils.NAVY_SLATE);

        JMenuItem itemReload = new JMenuItem("Tải lại toàn bộ dữ liệu từ tệp");
        itemReload.setFont(UIUtils.BODY_FONT);
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

        JMenuItem itemLogout = new JMenuItem("Đăng xuất tài khoản");
        itemLogout.setFont(UIUtils.BODY_FONT);
        itemLogout.addActionListener(e -> handleLogout());

        JMenuItem itemExit = new JMenuItem("Thoát chương trình");
        itemExit.setFont(UIUtils.BODY_FONT);
        itemExit.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát ứng dụng?", "Thoát", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        menuSystem.add(itemReload);
        menuSystem.add(itemLogout);
        menuSystem.addSeparator();
        menuSystem.add(itemExit);

        // Menu Trợ giúp
        JMenu menuHelp = new JMenu("Trợ giúp");
        menuHelp.setFont(UIUtils.BOLD_FONT);
        menuHelp.setForeground(UIUtils.NAVY_SLATE);

        JMenuItem itemAbout = new JMenuItem("Thông tin đồ án & Nhóm tác giả");
        itemAbout.setFont(UIUtils.BODY_FONT);
        itemAbout.addActionListener(e -> {
            String msg = "ĐỒ ÁN: HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI (MINIMART)\n\n" +
                    "• Môn học: Lập trình hướng đối tượng (Java)\n" +
                    "• Giảng viên hướng dẫn: ThS. Nguyễn Thị Tuyết Hải - PTIT HCM\n" +
                    "• Nhóm sinh viên thực hiện:\n" +
                    "   1. Lương Văn Thiện - N24DCCN076 (Trưởng nhóm)\n" +
                    "   2. Trần Nhật Nguyên - N22DCCN057\n" +
                    "   3. Nguyễn Hải Phong - N24DCCN059\n" +
                    "   4. Ngô Đức Thành - N24DCCN074\n\n" +
                    "• Kiến trúc: Mô hình MVC (Model - View - Controller)\n" +
                    "• Phân quyền người dùng: Quản trị viên (Admin) & Nhân viên thu ngân (Cashier)\n" +
                    "• Áp dụng OOP: Đóng gói, Kế thừa, Đa hình, Lớp trừu tượng, Giao diện (Interface),\n" +
                    "  Lambda, Ngoại lệ tùy chỉnh, Cấu trúc dữ liệu & Giao diện đồ họa Swing.\n\n" +
                    "© 2026 PTIT HCM - Đã đăng ký bản quyền.";
            JOptionPane.showMessageDialog(this, msg, "Giới thiệu đồ án", JOptionPane.INFORMATION_MESSAGE);
        });

        menuHelp.add(itemAbout);

        menuBar.add(menuSystem);
        menuBar.add(menuHelp);
        return menuBar;
    }
}
