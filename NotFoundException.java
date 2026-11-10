/**
 * Ngoại lệ ném ra khi không tìm thấy thực thể theo mã ID.
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}

