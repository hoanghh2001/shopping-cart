package hoang.shop.cart.mapper;

import hoang.shop.cart.dto.request.CartCreateRequest;
import hoang.shop.cart.dto.request.CartUpdateRequest;
import hoang.shop.cart.dto.response.CartResponse;
import hoang.shop.cart.model.Cart;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toEntity(CartCreateRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(CartUpdateRequest request, @MappingTarget Cart entity);
    @Mapping(target = "userId",source = "user.id")
    CartResponse toResponse(Cart entity);
}
