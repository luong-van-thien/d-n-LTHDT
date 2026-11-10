import java.io.IOException;

public interface Exportable {
    /**
     * Xuất dữ liệu đối tượng ra tệp tin (File Text / Hóa đơn).
     * @param destinationPath Đường dẫn lưu tệp
     * @return true nếu xuất thành công, ném IOException nếu lỗi
     * @throws IOException Ngoại lệ khi ghi tệp
     */
    boolean exportToFile(String destinationPath) throws IOException;
}

