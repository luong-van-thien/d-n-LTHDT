import javax.swing.*;

/**
 * Lớp khởi chạy ứng dụng chính (MainApp).
 * Chạy trên luồng Event Dispatch Thread (EDT) theo chuẩn bài giảng Swing (Slide Chapter 10).
 * Bắt đầu với màn hình Đăng nhập (LoginDialog) để xác thực và phân quyền RBAC.
 */
public class MainApp {
    public static void main(String[] args) {
        // Cấu hình giao diện Look and Feel theo hệ điều hành Windows hiện đại
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Khởi động giao diện trên luồng EDT
        SwingUtilities.invokeLater(() -> {
            UserRepository userRepo = new UserRepository();
            LoginDialog loginDialog = new LoginDialog(null, userRepo);
            loginDialog.setVisible(true);

            if (loginDialog.isSuccess() && loginDialog.getLoggedInUser() != null) {
                MainFrame frame = new MainFrame(userRepo, loginDialog.getLoggedInUser());
                frame.setVisible(true);
            } else {
                // Người dùng bấm Thoát hoặc đóng cửa sổ đăng nhập
                System.exit(0);
            }
        });
    }
}
