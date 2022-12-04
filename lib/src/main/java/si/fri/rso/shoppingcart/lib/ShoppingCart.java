package si.fri.rso.shoppingcart.lib;

import java.util.List;

public class ShoppingCart {

    private Integer cartId;
    private List<ShoppingCartProduct> products;

    public Integer getCartId() {
        return this.cartId;
    }

    public void setCartId(Integer id) {
        this.cartId = id;
    }

    public List<ShoppingCartProduct> getProducts() {
        return this.products;
    }

    public void setProducts(List<ShoppingCartProduct> products) {
        this.products = products;
    }
}
