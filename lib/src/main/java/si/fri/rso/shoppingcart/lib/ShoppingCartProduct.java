package si.fri.rso.shoppingcart.lib;

public class ShoppingCartProduct {

    private Integer id;
    private Integer productId;
    private Integer cartId;
    private Integer quantity;
    private String name;
    private String description;
    private String weight;
    private Boolean favourite;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }
}
