package hoang.shop.cart.mapper;

import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.dto.response.ItemResponse;
import hoang.shop.cart.model.CartItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "productVariant", ignore = true)
    @Mapping(target = "nameLabel", ignore = true)
    @Mapping(target = "sizeLabel", ignore = true)
    @Mapping(target = "colorLabel", ignore = true)
    @Mapping(target = "hexLabel", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "unitPriceBefore", ignore = true)
    @Mapping(target = "unitPriceAtOrder", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    CartItem toEntity(CartItemCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "productVariant", ignore = true)
    @Mapping(target = "nameLabel", ignore = true)
    @Mapping(target = "sizeLabel", ignore = true)
    @Mapping(target = "colorLabel", ignore = true)
    @Mapping(target = "hexLabel", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "unitPriceBefore", ignore = true)
    @Mapping(target = "unitPriceAtOrder", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void merge(CartItemUpdateRequest request,@MappingTarget CartItem entity);


    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "variantId", source = "productVariant.id")
    @Mapping(target = "nameLabel", source = "nameLabel")
    @Mapping(target = "colorLabel", source = "colorLabel")
    @Mapping(target = "hexLabel", source = "hexLabel")
    @Mapping(target = "sizeLabel", source = "sizeLabel")
    ItemResponse toResponse(CartItem item);


}
