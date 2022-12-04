package si.fri.rso.shoppingcart.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "shopping_cart_product", uniqueConstraints = {@UniqueConstraint(name = "UniqueProductInCart", columnNames = {"product_id", "cart_id"})})

public class ShoppingCartProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "cart_id")
    private Integer cartId;

    @Column(name = "quantity")
    private Integer quantity;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
