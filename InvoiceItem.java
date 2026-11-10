import java.io.Serializable;

/**
 * Đại diện cho một dòng sản phẩm trong hóa đơn mua hàng.
 */
public class InvoiceItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Product product;
    private int quantity;
    private double unitPrice;

    public InvoiceItem() {
    }

    public InvoiceItem(Product product, int quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Số lượng mua phải lớn hơn 0!");
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubTotal() {
        return quantity * unitPrice;
    }

    public double getEstimatedProfit() {
        if (product == null) return 0.0;
        return (unitPrice - product.getImportPrice()) * quantity;
    }
}

