package hoang.shop.cart.mapper;

import hoang.shop.cart.dto.request.CartUpdateRequest;
import hoang.shop.cart.dto.response.CartResponse;
import hoang.shop.cart.model.Cart;
import org.mapstruct.*;

@Mapper(componentModel = "spring",uses = { CartItemMapper.class } )
public interface CartMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(CartUpdateRequest request, @MappingTarget Cart entity);


    CartResponse toResponse(Cart entity);
}
