package si.fri.rso.shoppingcart.models.converters;

import si.fri.rso.shoppingcart.lib.ShoppingCart;
import si.fri.rso.shoppingcart.models.entities.ShoppingCartEntity;

public class ShoppingCartConverter {

    public static ShoppingCart toDto(ShoppingCartEntity entity) {

        ShoppingCart dto = new ShoppingCart();
        dto.setImageId(entity.getId());
        dto.setCreated(entity.getCreated());
        dto.setDescription(entity.getDescription());
        dto.setTitle(entity.getTitle());
        dto.setHeight(entity.getHeight());
        dto.setWidth(entity.getWidth());
        dto.setUri(entity.getUri());

        return dto;

    }

    public static ShoppingCartEntity toEntity(ShoppingCart dto) {

        ShoppingCartEntity entity = new ShoppingCartEntity();
        entity.setCreated(dto.getCreated());
        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());
        entity.setHeight(dto.getHeight());
        entity.setWidth(dto.getWidth());
        entity.setUri(dto.getUri());

        return entity;

    }

}
