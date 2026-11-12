import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Quản lý Lưu trữ và Truy xuất Tài khoản Người dùng (UserRepository).
 * Đọc/Ghi dữ liệu từ tệp users.csv và tự động khởi tạo các tài khoản mặc định.
 * Áp dụng Factory Method để khởi tạo đối tượng AdminUser hoặc CashierUser theo Role.
 */
public class UserRepository {
    private final List<User> users = new ArrayList<>();
    private final String filePath = "users.csv";

    public UserRepository() {
        loadFromFile();
    }

    public synchronized void loadFromFile() {
        users.clear();
        File file = new File(filePath);
        if (!file.exists()) {
            initDefaultUsers();
            saveToFile();
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Format: username,password,fullName,ROLE
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String fullName = parts[2].trim();
                    String roleStr = parts[3].trim().toUpperCase();

                    Role role = Role.ADMIN;
                    try {
                        role = Role.valueOf(roleStr);
                    } catch (IllegalArgumentException ignored) {}

                    User user = createUser(username, password, fullName, role);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file users.csv: " + e.getMessage());
            initDefaultUsers();
        }

        if (users.isEmpty()) {
            initDefaultUsers();
            saveToFile();
        }
    }

    public synchronized void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write("# username,password,fullName,role\n");
            for (User u : users) {
                writer.write(String.format("%s,%s,%s,%s\n",
                        u.getUsername(),
                        u.getPassword(),
                        u.getFullName(),
                        u.getRole().name()));
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file users.csv: " + e.getMessage());
        }
    }

    /**
     * Factory Method tạo đối tượng User đa hình dựa vào Role.
     */
    public static User createUser(String username, String password, String fullName, Role role) {
        if (role == Role.ADMIN) {
            return new AdminUser(username, password, fullName);
        } else {
            return new CashierUser(username, password, fullName);
        }
    }

    /**
     * Xác thực thông tin đăng nhập.
     * @return Đối tượng User nếu đúng mật khẩu, ngược lại trả về null.
     */
    public synchronized User authenticate(String username, String password) {
        if (username == null || password == null) return null;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username.trim()) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public synchronized List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public synchronized User getUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public synchronized void addUser(User user) {
        users.removeIf(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
        users.add(user);
        saveToFile();
    }

    private void initDefaultUsers() {
        users.clear();
        users.add(new AdminUser("admin", "admin123", "Lương Văn Thiện (Quản trị viên)"));
        users.add(new CashierUser("cashier", "cashier123", "Nhân viên Thu Ngân"));
        users.add(new AdminUser("ptit", "123456", "ThS. Nguyễn Thị Tuyết Hải (Giảng viên)"));
    }
}

