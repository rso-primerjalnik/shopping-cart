package si.fri.rso.shoppingcart.models.converters;

import si.fri.rso.shoppingcart.lib.ShoppingCart;
import si.fri.rso.shoppingcart.models.entities.ShoppingCartEntity;

import java.util.stream.Collectors;

public class ShoppingCartConverter {

    public static ShoppingCart toDto(ShoppingCartEntity entity) {

        ShoppingCart dto = new ShoppingCart();
        dto.setCartId(entity.getId());
        dto.setProducts(entity.getProducts().stream().map(ShoppingCartProductConverter::toDto).collect(Collectors.toList()));

        return dto;
    }

    public static ShoppingCartEntity toEntity(ShoppingCart dto) {

        ShoppingCartEntity entity = new ShoppingCartEntity();
        entity.setId(dto.getCartId());
        entity.setProducts(dto.getProducts().stream().map(ShoppingCartProductConverter::toEntity).collect(Collectors.toList()));

        return entity;
    }

}
