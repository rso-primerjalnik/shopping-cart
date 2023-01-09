package si.fri.rso.shoppingcart.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.shoppingcart.lib.Product;
import si.fri.rso.shoppingcart.lib.ShoppingCart;
import si.fri.rso.shoppingcart.lib.ShoppingCartProduct;
import si.fri.rso.shoppingcart.models.converters.ShoppingCartConverter;
import si.fri.rso.shoppingcart.models.entities.ShoppingCartEntity;
import si.fri.rso.shoppingcart.services.config.RestProperties;


@RequestScoped
public class ShoppingCartBean {

    private Logger log = Logger.getLogger(ShoppingCartBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private RestProperties properties;

    public List<ShoppingCart> getShoppingCarts() {

        TypedQuery<ShoppingCartEntity> query = em.createNamedQuery(
                "ShoppingCartEntity.getAll", ShoppingCartEntity.class);

        List<ShoppingCartEntity> resultList = query.getResultList();

        return resultList.stream().map(ShoppingCartConverter::toDto).collect(Collectors.toList());
    }

    @Timed
    public ShoppingCart getShoppingCartById(Integer id) {

        ShoppingCartEntity shoppingCartEntity = em.find(ShoppingCartEntity.class, id);

        if (shoppingCartEntity == null) {
            throw new NotFoundException();
        }

        ShoppingCart shoppingCart = ShoppingCartConverter.toDto(shoppingCartEntity);

        return shoppingCart;
    }

    public ShoppingCart createShoppingCart(ShoppingCart shoppingCart) {

        ShoppingCartEntity shoppingCartEntity = ShoppingCartConverter.toEntity(shoppingCart);

        try {
            beginTx();
            em.persist(shoppingCartEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (shoppingCartEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return ShoppingCartConverter.toDto(shoppingCartEntity);
    }

    public ShoppingCart putShoppingCart(Integer id, ShoppingCart shoppingCart) {

        ShoppingCartEntity c = em.find(ShoppingCartEntity.class, id);

        if (c == null) {
            return null;
        }

        ShoppingCartEntity updatedShoppingCartEntity = ShoppingCartConverter.toEntity(shoppingCart);

        try {
            beginTx();
            updatedShoppingCartEntity.setId(c.getId());
            updatedShoppingCartEntity = em.merge(updatedShoppingCartEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return ShoppingCartConverter.toDto(updatedShoppingCartEntity);
    }

    public boolean deleteShoppingCart(Integer id) {

        ShoppingCartEntity shoppingCart = em.find(ShoppingCartEntity.class, id);

        if (shoppingCart != null) {
            try {
                beginTx();
                em.remove(shoppingCart);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    public ShoppingCart setAdditionalDataForCartProducts(ShoppingCart shoppingCart) {
        String productCatalogBaseUrl = properties.getProductCatalogBaseUrl();

        List<Product> products = ClientBuilder.newClient()
                .target(productCatalogBaseUrl + "/v1/products/filter")
                .queryParam("filter", "id:IN:" + shoppingCart.getProducts().stream().map(ShoppingCartProduct::getProductId).collect(Collectors.toList()))
                .request()
                .get(new GenericType<>() {
                });

        HashMap<Integer, Product> productsHash = new HashMap<>();
        for (Product product : products) {
            productsHash.put(product.getId(), product);
        }

        for (ShoppingCartProduct scProduct : shoppingCart.getProducts()) {
            Product productData = productsHash.get(scProduct.getProductId());
            if (productData != null) {
                scProduct.setName(productData.getName());
                scProduct.setDescription(productData.getDescription());
                scProduct.setWeight(productData.getWeight());
            }
        }

        return shoppingCart;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
