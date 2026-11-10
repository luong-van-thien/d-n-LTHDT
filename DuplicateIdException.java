/**
 * Ngoại lệ ném ra khi cố gắng thêm mới đối tượng có mã ID đã tồn tại trong hệ thống.
 */
public class DuplicateIdException extends Exception {
    public DuplicateIdException(String message) {
        super(message);
    }
}

