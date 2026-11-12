import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Cửa sổ Đăng nhập Hệ thống (LoginDialog).
 * Tích hợp Phân quyền Người dùng (RBAC - Role-Based Access Control).
 * Cung cấp nút đăng nhập nhanh 1-Click phục vụ việc chấm bài & báo cáo đồ án.
 */
public class LoginDialog extends JDialog {
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
        setSize(460, 420);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 1. BANNER TIÊU ĐỀ
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(new Color(24, 43, 73));
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ SIÊU THỊ MINIMART", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Học viện Công nghệ Bưu chính Viễn thông (PTIT HCM)", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(new Color(200, 215, 235));

        bannerPanel.add(lblTitle, BorderLayout.NORTH);
        bannerPanel.add(Box.createVerticalStrut(4), BorderLayout.CENTER);
        bannerPanel.add(lblSub, BorderLayout.SOUTH);
        add(bannerPanel, BorderLayout.NORTH);

        // 2. KHUNG NHẬP LIỆU CHÍNH
        JPanel centerPanel = new JPanel(null);
        centerPanel.setBackground(Color.WHITE);

        JLabel lblInstruction = new JLabel("Vui lòng đăng nhập để truy cập hệ thống:");
        lblInstruction.setFont(UIUtils.BOLD_FONT);
        lblInstruction.setForeground(new Color(30, 41, 59));
        lblInstruction.setBounds(35, 15, 380, 25);
        centerPanel.add(lblInstruction);

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(UIUtils.BODY_FONT);
        lblUser.setBounds(35, 50, 120, 25);
        centerPanel.add(lblUser);

        txtUsername = new JTextField("admin");
        txtUsername.setFont(UIUtils.BODY_FONT);
        txtUsername.setBounds(155, 48, 260, 30);
        centerPanel.add(txtUsername);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(UIUtils.BODY_FONT);
        lblPass.setBounds(35, 90, 120, 25);
        centerPanel.add(lblPass);

        txtPassword = new JPasswordField("admin123");
        txtPassword.setFont(UIUtils.BODY_FONT);
        txtPassword.setBounds(155, 88, 260, 30);
        centerPanel.add(txtPassword);

        // Nút Đăng nhập & Thoát
        btnLogin = UIUtils.createStyledButton("ĐĂNG NHẬP", UIUtils.PRIMARY_COLOR, Color.WHITE);
        btnLogin.setFont(UIUtils.BOLD_FONT);
        btnLogin.setBounds(155, 130, 140, 35);
        centerPanel.add(btnLogin);

        btnCancel = UIUtils.createStyledButton("Thoát", UIUtils.GRAY_COLOR, Color.WHITE);
        btnCancel.setBounds(305, 130, 110, 35);
        centerPanel.add(btnCancel);

        // Đường kẻ phân cách
        JSeparator sep = new JSeparator();
        sep.setBounds(35, 180, 380, 2);
        centerPanel.add(sep);

        // PHẦN DEMO / CHẤM BÀI 1-CLICK
        JLabel lblQuick = new JLabel("Đăng nhập nhanh (Dành cho Giảng viên & Thử nghiệm):");
        lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQuick.setForeground(new Color(100, 116, 139));
        lblQuick.setBounds(35, 190, 380, 22);
        centerPanel.add(lblQuick);

        btnQuickAdmin = UIUtils.createStyledButton("⚡ Vào quyền Quản lý (Admin)", new Color(13, 110, 253), Color.WHITE);
        btnQuickAdmin.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnQuickAdmin.setBounds(35, 218, 185, 34);
        centerPanel.add(btnQuickAdmin);

        btnQuickCashier = UIUtils.createStyledButton("⚡ Vào quyền Thu ngân (Cashier)", new Color(22, 163, 74), Color.WHITE);
        btnQuickCashier.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnQuickCashier.setBounds(230, 218, 185, 34);
        centerPanel.add(btnQuickCashier);

        JLabel lblHint = new JLabel("<html><i>• <b>Admin:</b> Xem toàn bộ 4 tab, sửa kho & xem báo cáo tài chính.<br>• <b>Cashier:</b> Chỉ bán hàng POS, xem lịch sử & khóa tab thống kê.</i></html>");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint.setForeground(new Color(100, 116, 139));
        lblHint.setBounds(35, 258, 380, 40);
        centerPanel.add(lblHint);

        add(centerPanel, BorderLayout.CENTER);

        // SỰ KIỆN NÚT BẤM
        btnLogin.addActionListener(e -> performLogin());
        btnCancel.addActionListener(e -> {
            success = false;
            dispose();
        });

        // Nhấn Enter ở ô textfield -> Tự động đăng nhập
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

        // Đăng nhập nhanh
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
