public interface Discountable {
    /**
     * Tính toán số tiền được giảm giá dựa trên tổng tiền ban đầu.
     * @param amount Tổng tiền hàng ban đầu
     * @return Số tiền được chiết khấu/giảm giá
     */
    double calculateDiscount(double amount);
}

