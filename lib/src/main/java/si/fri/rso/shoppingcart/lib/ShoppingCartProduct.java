package si.fri.rso.shoppingcart.lib;

public class ShoppingCartProduct {

    private Integer productId;
    private Integer cartId;
    private Integer quantity;

    public Integer getProductId() {
        return this.productId;
    }

    public void setProductId(Integer id) {
        this.productId = id;
    }

    public Integer getCartId() {
        return this.cartId;
    }

    public void setCartId(Integer id) {
        this.cartId = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
