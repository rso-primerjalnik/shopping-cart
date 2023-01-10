package si.fri.rso.shoppingcart.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.shoppingcart.lib.Product;
import si.fri.rso.shoppingcart.lib.ShoppingCart;
import si.fri.rso.shoppingcart.lib.ShoppingCartProduct;
import si.fri.rso.shoppingcart.models.converters.ShoppingCartConverter;
import si.fri.rso.shoppingcart.models.entities.ShoppingCartEntity;
import si.fri.rso.shoppingcart.models.entities.ShoppingCartProductEntity;
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

    @Metered(name = "add_product_to_cart_metered")
    public ShoppingCart addProductToCart(Integer id, ShoppingCartProduct product) {

        // Check if shopping cart exists
        ShoppingCartEntity existingCart = em.find(ShoppingCartEntity.class, id);
        if (existingCart == null)
            return null;

        // Check if product exists - calls product service
        String productCatalogBaseUrl = properties.getProductCatalogBaseUrl();
        if (productCatalogBaseUrl != null) {
            String apiUrl = productCatalogBaseUrl + "/v1/products/" + product.getProductId();
            Response response = ClientBuilder.newClient().target(apiUrl).request().get();

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new NotFoundException("Product with provided id was not found");
            }
        }

        // Get product from cart if it was already inserted
        ShoppingCartProductEntity existingProduct = null;
        try {
            existingProduct = em.createNamedQuery("ShoppingCartProductEntity.find", ShoppingCartProductEntity.class)
                    .setParameter("cartId", id)
                    .setParameter("productId", product.getProductId())
                    .getSingleResult();
        }catch (Exception e) {}

        int quantity = 0;
        if (product.getQuantity() != null) {
            if (product.getQuantity() <= 0)
                quantity = 0;
            else
                quantity = product.getQuantity();
        }


        if (existingProduct == null && quantity == 0) {
            throw new IllegalArgumentException("Quantity can't be 0 for new product");
        }

        // Remove product from cart
        if (existingProduct != null && quantity == 0) {
            try {
                beginTx();
                existingCart.getProducts().remove(existingProduct);
                em.remove(existingProduct);
                em.persist(existingCart);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
            return ShoppingCartConverter.toDto(existingCart);
        }

        ShoppingCartProductEntity editableProduct = existingProduct;

        if (existingProduct == null) {
            editableProduct = new ShoppingCartProductEntity();
            editableProduct.setCartId(product.getCartId());
            editableProduct.setProductId(product.getProductId());

            existingCart.getProducts().add(editableProduct);
        }

        // Change quantity for existing product, or set it for new one
        editableProduct.setQuantity(product.getQuantity());

        // Add new product with quantity, or just change existing one
        try {
            beginTx();
            em.persist(editableProduct);
            em.persist(existingCart);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return ShoppingCartConverter.toDto(existingCart);
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

    @Timed(name = "set_additional_data_for_cart_products_timer")
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 2)
    @Fallback(fallbackMethod = "setAdditionalDataForCartProductsFallback")
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
                scProduct.setFavourite(productData.getFavourite());
            }
        }

        return shoppingCart;
    }

    public ShoppingCart setAdditionalDataForCartProductsFallback(ShoppingCart shoppingCart) {
        return null;
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
