import javax.swing.*;

/**
 * Lớp khởi chạy ứng dụng chính (MainApp).
 * Chạy trên luồng Event Dispatch Thread (EDT) theo chuẩn bài giảng Swing (Slide Chapter 10).
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
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

