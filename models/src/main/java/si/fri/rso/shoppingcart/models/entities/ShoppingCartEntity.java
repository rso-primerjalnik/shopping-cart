package si.fri.rso.shoppingcart.models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "shopping_cart")
@NamedQueries(value = {@NamedQuery(name = "ShoppingCartEntity.getAll", query = "SELECT carts FROM ShoppingCartEntity carts")})

public class ShoppingCartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<ShoppingCartProductEntity> products;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShoppingCartProductEntity> getProducts() {
        return this.products;
    }

    public void setProducts(List<ShoppingCartProductEntity> products) {
        this.products = products;
    }
}