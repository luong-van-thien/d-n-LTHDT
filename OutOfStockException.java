/**
 * Ngoại lệ ném ra khi số lượng sản phẩm yêu cầu vượt quá số lượng hàng tồn trong kho.
 */
public class OutOfStockException extends Exception {
    public OutOfStockException(String message) {
        super(message);
    }
}

