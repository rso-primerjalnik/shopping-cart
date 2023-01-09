package si.fri.rso.shoppingcart.lib;

import java.util.List;

public class ShoppingCart {

    private Integer cartId;
    private String name;
    private List<ShoppingCartProduct> products;

    public Integer getCartId() {
        return this.cartId;
    }

    public void setCartId(Integer id) {
        this.cartId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShoppingCartProduct> getProducts() {
        return this.products;
    }

    public void setProducts(List<ShoppingCartProduct> products) {
        this.products = products;
    }
}
