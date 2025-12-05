package hoang.shop.cart.mapper;

import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.dto.response.CartItemResponse;
import hoang.shop.cart.model.CartItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItem toEntity(CartItemCreateRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(CartItemUpdateRequest request,@MappingTarget CartItem entity);
    @Mapping(target = "productVariantId", source = "productVariant.id")
    CartItemResponse toResponse(CartItem cartItem);
}
