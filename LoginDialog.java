import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Cửa sổ Đăng nhập Hệ thống (LoginDialog) chuẩn giao diện Enterprise:
 * - Header màu Navy Slate chuyển sắc (Gradient) sang trọng.
 * - Form nhập liệu bo tròn mềm mại, hiệu ứng viền phát sáng khi chọn ô (Focus Glow).
 * - Nút bấm lớn, bo góc hiện đại với độ nảy và màu sắc trực quan.
 * - Phân khu Đăng nhập nhanh dành cho Giảng viên & Hội đồng nghiệm thu trực quan 1-Click.
 */
public class LoginDialog extends JDialog {
    // Cờ cấu hình: true để hiện nút demo cho thầy cô chấm bài, false để đóng gói .jar thương mại
    public static boolean SHOW_DEMO_BUTTONS = true;

    private final UserRepository userRepository;
    private User loggedInUser = null;
    private boolean success = false;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JButton btnQuickAdmin;
    private JButton btnQuickCashier;

    public LoginDialog(Frame parent, UserRepository userRepository) {
        super(parent, "ĐĂNG NHẬP HỆ THỐNG - MINIMART SYSTEM", true);
        this.userRepository = userRepository;

        initComponents();
    }

    private void initComponents() {
        if (SHOW_DEMO_BUTTONS) {
            setSize(520, 480);
        } else {
            setSize(480, 310);
        }

        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // =========================================================================
        // 1. HEADER BANNER CHUYỂN SẮC (GRADIENT BANNER)
        // =========================================================================
        JPanel bannerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntialiasing(g2);
                GradientPaint gp = new GradientPaint(0, 0, UIUtils.NAVY_DARK, getWidth(), getHeight(), UIUtils.NAVY_SLATE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Vẽ logo siêu thị mini cách điệu
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(getWidth() - 80, -20, 100, 100);

                // Điểm nhấn viền màu xanh dương tinh tế ở đáy banner
                g2.setColor(UIUtils.PRIMARY_COLOR);
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        bannerPanel.setOpaque(false);
        bannerPanel.setLayout(new BorderLayout());
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ BÁN HÀNG & KHO MINI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setIcon(UIUtils.VectorIcons.cart(22, new Color(96, 165, 250))); // Biểu tượng giỏ hàng xanh sáng
        lblTitle.setIconTextGap(10);

        JLabel lblSub = new JLabel("Học viện Công nghệ Bưu chính Viễn thông (PTIT HCM) • Đồ án OOP");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(203, 213, 225));

        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        bannerPanel.add(titleBox, BorderLayout.CENTER);
        add(bannerPanel, BorderLayout.NORTH);

        // =========================================================================
        // 2. KHUNG NHẬP LIỆU CHÍNH (INPUT FORM)
        // =========================================================================
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 36, 16, 36));

        JLabel lblInstruction = new JLabel("Vui lòng đăng nhập thông tin tài khoản:");
        lblInstruction.setFont(UIUtils.BOLD_FONT);
        lblInstruction.setForeground(UIUtils.NAVY_SLATE);
        lblInstruction.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblInstruction);
        centerPanel.add(Box.createVerticalStrut(14));

        // Dòng Username
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(UIUtils.SMALL_BOLD);
        lblUser.setForeground(UIUtils.SLATE_TEXT);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblUser);
        centerPanel.add(Box.createVerticalStrut(4));

        txtUsername = new JTextField("admin");
        UIUtils.styleTextField(txtUsername);
        txtUsername.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(txtUsername);
        centerPanel.add(Box.createVerticalStrut(12));

        // Dòng Password
        JLabel lblPass = new JLabel("Mật khẩu truy cập:");
        lblPass.setFont(UIUtils.SMALL_BOLD);
        lblPass.setForeground(UIUtils.SLATE_TEXT);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblPass);
        centerPanel.add(Box.createVerticalStrut(4));

        txtPassword = new JPasswordField("admin123");
        txtPassword.setFont(UIUtils.BODY_FONT);
        UIUtils.styleTextField(txtPassword);
        txtPassword.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(txtPassword);
        centerPanel.add(Box.createVerticalStrut(18));

        // Hàng nút Đăng nhập & Thoát
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        btnCancel = UIUtils.createStyledButton("Thoát", UIUtils.GRAY_COLOR, Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(100, 36));

        btnLogin = UIUtils.createStyledButton("ĐĂNG NHẬP", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(140, 36));

        btnRow.add(btnCancel);
        btnRow.add(btnLogin);
        centerPanel.add(btnRow);

        // =========================================================================
        // 3. KHU VỰC DEMO / CHẤM BÀI 1-CLICK CHO GIẢNG VIÊN
        // =========================================================================
        if (SHOW_DEMO_BUTTONS) {
            centerPanel.add(Box.createVerticalStrut(16));
            JSeparator sep = new JSeparator();
            sep.setForeground(UIUtils.BORDER_COLOR);
            sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 2));
            sep.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerPanel.add(sep);
            centerPanel.add(Box.createVerticalStrut(12));

            JLabel lblQuick = new JLabel("Đăng nhập nhanh (Dành cho Giảng viên & Thử nghiệm):");
            lblQuick.setFont(UIUtils.SMALL_BOLD);
            lblQuick.setForeground(UIUtils.SLATE_TEXT);
            lblQuick.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerPanel.add(lblQuick);
            centerPanel.add(Box.createVerticalStrut(8));

            JPanel demoButtonGrid = new JPanel(new GridLayout(1, 2, 10, 0));
            demoButtonGrid.setOpaque(false);
            demoButtonGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
            demoButtonGrid.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));

            btnQuickAdmin = UIUtils.createStyledButton("Quản trị viên (Admin)", UIUtils.PRIMARY_COLOR, Color.WHITE);
            btnQuickAdmin.setFont(new Font("Segoe UI", Font.BOLD, 12));

            btnQuickCashier = UIUtils.createStyledButton("Thu ngân (Cashier)", UIUtils.SUCCESS_COLOR, Color.WHITE);
            btnQuickCashier.setFont(new Font("Segoe UI", Font.BOLD, 12));

            demoButtonGrid.add(btnQuickAdmin);
            demoButtonGrid.add(btnQuickCashier);
            centerPanel.add(demoButtonGrid);
            centerPanel.add(Box.createVerticalStrut(8));

            JLabel lblHint = new JLabel("<html><i>• <b>Admin:</b> Toàn quyền 4 mục (Kho, POS, Hóa đơn, Thống kê).<br>• <b>Cashier:</b> Chỉ bán hàng POS & xem lịch sử (Khóa mục Kho & Thống kê).</i></html>");
            lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblHint.setForeground(UIUtils.SLATE_MUTED);
            lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerPanel.add(lblHint);

            btnQuickAdmin.addActionListener(e -> {
                txtUsername.setText("admin");
                txtPassword.setText("admin123");
                performLogin();
            });

            btnQuickCashier.addActionListener(e -> {
                txtUsername.setText("cashier");
                txtPassword.setText("cashier123");
                performLogin();
            });
        }

        add(centerPanel, BorderLayout.CENTER);

        // =========================================================================
        // 4. SỰ KIỆN NÚT BẤM VÀ PHÍM TẮT ENTER
        // =========================================================================
        btnLogin.addActionListener(e -> performLogin());
        btnCancel.addActionListener(e -> {
            success = false;
            dispose();
        });

        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        User user = userRepository.authenticate(username, password);
        if (user != null) {
            this.loggedInUser = user;
            this.success = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không chính xác!\n(Tài khoản mẫu: admin / admin123 hoặc cashier / cashier123)",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            txtPassword.selectAll();
            txtPassword.requestFocus();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
