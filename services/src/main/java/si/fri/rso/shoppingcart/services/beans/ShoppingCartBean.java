package si.fri.rso.shoppingcart.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import si.fri.rso.shoppingcart.lib.ShoppingCart;
import si.fri.rso.shoppingcart.models.converters.ShoppingCartConverter;
import si.fri.rso.shoppingcart.models.entities.ShoppingCartEntity;


@RequestScoped
public class ShoppingCartBean {

    private Logger log = Logger.getLogger(ShoppingCartBean.class.getName());

    @Inject
    private EntityManager em;

    public List<ShoppingCart> getImageMetadata() {

        TypedQuery<ShoppingCartEntity> query = em.createNamedQuery(
                "ImageMetadataEntity.getAll", ShoppingCartEntity.class);

        List<ShoppingCartEntity> resultList = query.getResultList();

        return resultList.stream().map(ShoppingCartConverter::toDto).collect(Collectors.toList());

    }

    public List<ShoppingCart> getImageMetadataFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, ShoppingCartEntity.class, queryParameters).stream()
                .map(ShoppingCartConverter::toDto).collect(Collectors.toList());
    }

    public ShoppingCart getImageMetadata(Integer id) {

        ShoppingCartEntity imageMetadataEntity = em.find(ShoppingCartEntity.class, id);

        if (imageMetadataEntity == null) {
            throw new NotFoundException();
        }

        ShoppingCart imageMetadata = ShoppingCartConverter.toDto(imageMetadataEntity);

        return imageMetadata;
    }

    public ShoppingCart createImageMetadata(ShoppingCart imageMetadata) {

        ShoppingCartEntity imageMetadataEntity = ShoppingCartConverter.toEntity(imageMetadata);

        try {
            beginTx();
            em.persist(imageMetadataEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (imageMetadataEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return ShoppingCartConverter.toDto(imageMetadataEntity);
    }

    public ShoppingCart putImageMetadata(Integer id, ShoppingCart imageMetadata) {

        ShoppingCartEntity c = em.find(ShoppingCartEntity.class, id);

        if (c == null) {
            return null;
        }

        ShoppingCartEntity updatedImageMetadataEntity = ShoppingCartConverter.toEntity(imageMetadata);

        try {
            beginTx();
            updatedImageMetadataEntity.setId(c.getId());
            updatedImageMetadataEntity = em.merge(updatedImageMetadataEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return ShoppingCartConverter.toDto(updatedImageMetadataEntity);
    }

    public boolean deleteImageMetadata(Integer id) {

        ShoppingCartEntity imageMetadata = em.find(ShoppingCartEntity.class, id);

        if (imageMetadata != null) {
            try {
                beginTx();
                em.remove(imageMetadata);
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
