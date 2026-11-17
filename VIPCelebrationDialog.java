import javax.swing.*;
import java.awt.*;

/**
 * Hộp thoại Vinh danh & Nâng Hạng Khách Hàng VIP (VIPCelebrationDialog):
 * - Bật lên tự động ngay khi khách hàng tích lũy chi tiêu vượt mốc chuẩn (>= 2.000.000 VNĐ).
 * - Minh chứng thực tiễn xuất sắc cho cơ chế chuyển đổi đối tượng và tính Đa hình trong OOP.
 * - Giao diện vinh danh màu Hoàng kim (Gold / Amber) với biểu tượng vương miện rực rỡ.
 */
public class VIPCelebrationDialog extends JDialog {
    private final VIPCustomer vipCustomer;

    public VIPCelebrationDialog(Window parent, VIPCustomer vipCustomer) {
        super(parent, "VINH DANH KHÁCH HÀNG THÂN THIẾT - NÂNG HẠNG VIP", ModalityType.APPLICATION_MODAL);
        this.vipCustomer = vipCustomer;

        initComponents();
    }

    private void initComponents() {
        setSize(520, 440);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // =========================================================================
        // 1. HEADER HOÀNG KIM (GOLD GRADIENT BANNER)
        // =========================================================================
        JPanel bannerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntialiasing(g2);

                GradientPaint gp = new GradientPaint(0, 0, new Color(217, 119, 6), getWidth(), getHeight(), new Color(180, 83, 9));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Vẽ vương miện hoàng gia vàng kim
                int cx = getWidth() / 2;
                int cy = 28;
                g2.setColor(new Color(254, 240, 138)); // Vàng nhạt tỏa sáng
                g2.fillOval(cx - 24, cy - 20, 48, 48);

                // Đường viền đáy
                g2.setColor(new Color(251, 191, 36));
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        bannerPanel.setLayout(new BoxLayout(bannerPanel, BoxLayout.Y_AXIS));
        bannerPanel.setOpaque(false);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel lblCrown = new JLabel("★ ★ ★", JLabel.CENTER);
        lblCrown.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCrown.setForeground(new Color(254, 240, 138));
        lblCrown.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("CHÚC MỪNG KHÁCH HÀNG NÂNG HẠNG VIP!", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Chính sách Khách Hàng Thân Thiết • MiniMart Loyalty Program", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(new Color(254, 243, 199));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        bannerPanel.add(lblCrown);
        bannerPanel.add(Box.createVerticalStrut(4));
        bannerPanel.add(lblTitle);
        bannerPanel.add(Box.createVerticalStrut(2));
        bannerPanel.add(lblSub);
        add(bannerPanel, BorderLayout.NORTH);

        // =========================================================================
        // 2. KHUNG NỘI DUNG VINH DANH (CELEBRATION BODY)
        // =========================================================================
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(18, 28, 16, 28));

        // Thông tin khách hàng
        JLabel lblCustName = new JLabel(String.format("Khách hàng: %s (%s)", vipCustomer.getName(), vipCustomer.getCustomerId()));
        lblCustName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCustName.setForeground(UIUtils.NAVY_DARK);
        lblCustName.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblCustName);
        centerPanel.add(Box.createVerticalStrut(10));

        // So sánh chuyển đổi thứ hạng (Before & After)
        JPanel cardRank = new JPanel(new GridLayout(1, 2, 10, 0));
        cardRank.setOpaque(false);
        cardRank.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Hạng cũ
        JPanel oldRank = createTierBadge("Hạng cũ", "Khách Thường (0%)", new Color(241, 245, 249), UIUtils.SLATE_TEXT);
        // Hạng mới
        JPanel newRank = createTierBadge("Hạng mới", "⭐ KHÁCH HÀNG VIP (5%)", new Color(254, 243, 199), new Color(180, 83, 9));

        cardRank.add(oldRank);
        cardRank.add(newRank);
        centerPanel.add(cardRank);
        centerPanel.add(Box.createVerticalStrut(14));

        // Thống kê tích lũy đạt chuẩn
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 10, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblSpent = new JLabel("Tổng chi tiêu: " + UIUtils.formatCurrency(vipCustomer.getTotalSpent()));
        lblSpent.setFont(UIUtils.BOLD_FONT);
        lblSpent.setForeground(UIUtils.NAVY_SLATE);

        JLabel lblPoints = new JLabel("Điểm Loyalty: " + vipCustomer.getLoyaltyPoints() + " điểm");
        lblPoints.setFont(UIUtils.BOLD_FONT);
        lblPoints.setForeground(UIUtils.SUCCESS_COLOR);

        statsRow.add(lblSpent);
        statsRow.add(lblPoints);
        centerPanel.add(statsRow);
        centerPanel.add(Box.createVerticalStrut(12));

        // Danh sách đặc quyền VIP
        JPanel benefitsPanel = new JPanel();
        benefitsPanel.setLayout(new BoxLayout(benefitsPanel, BoxLayout.Y_AXIS));
        benefitsPanel.setBackground(new Color(248, 250, 252));
        benefitsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblBenefitTitle = new JLabel("ĐẶC QUYỀN DÀNH RIÊNG CHO HỘI VIÊN VIP:");
        lblBenefitTitle.setFont(UIUtils.SMALL_BOLD);
        lblBenefitTitle.setForeground(new Color(180, 83, 9));
        benefitsPanel.add(lblBenefitTitle);
        benefitsPanel.add(Box.createVerticalStrut(4));

        benefitsPanel.add(createBenefitRow("✓  Tự động giảm 5% trên mọi đơn hàng thanh toán tiếp theo."));
        benefitsPanel.add(createBenefitRow("✓  Tích lũy 1 điểm Loyalty cho mỗi 10.000 VNĐ chi tiêu."));
        benefitsPanel.add(createBenefitRow("✓  Nhận quà tri ân sinh nhật và ưu đãi đặc quyền từ MiniMart."));

        centerPanel.add(benefitsPanel);
        add(centerPanel, BorderLayout.CENTER);

        // =========================================================================
        // 3. NÚT XÁC NHẬN
        // =========================================================================
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR));

        JButton btnConfirm = UIUtils.createStyledButton("XÁC NHẬN & CẬP NHẬT THẺ VIP", new Color(217, 119, 6), Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirm.setPreferredSize(new Dimension(280, 38));
        btnConfirm.addActionListener(e -> dispose());

        bottomBar.add(btnConfirm);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JPanel createTierBadge(String label, String rankName, Color bg, Color fg) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntialiasing(g2);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(203, 213, 225));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JLabel lblL = new JLabel(label);
        lblL.setFont(UIUtils.SMALL_FONT);
        lblL.setForeground(UIUtils.SLATE_TEXT);

        JLabel lblR = new JLabel(rankName);
        lblR.setFont(UIUtils.BOLD_FONT);
        lblR.setForeground(fg);

        p.add(lblL);
        p.add(lblR);
        return p;
    }

    private JLabel createBenefitRow(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIUtils.BODY_FONT);
        lbl.setForeground(UIUtils.NAVY_SLATE);
        lbl.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return lbl;
    }
}
