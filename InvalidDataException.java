/**
 * Ngoại lệ ném ra khi dữ liệu người dùng nhập không hợp lệ (để trống, giá trị âm, sai định dạng).
 */
public class InvalidDataException extends Exception {
    public InvalidDataException(String message) {
        super(message);
    }
}

