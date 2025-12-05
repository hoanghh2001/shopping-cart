    package hoang.shop.order.mapper;

    import hoang.shop.config.MapStructConfig;
    import hoang.shop.order.dto.request.OderItemUpdateRequest;
    import hoang.shop.order.dto.request.OrderItemCreateRequest;
    import hoang.shop.order.dto.response.OrderItemResponse;
    import hoang.shop.order.model.OrderItem;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;
    import org.mapstruct.MappingTarget;

    @Mapper(componentModel = "spring", config = MapStructConfig.class)

    public interface OrderItemMapper {
        OrderItem toEntity(OrderItemCreateRequest request);
        void merger(@MappingTarget OrderItem entity, OderItemUpdateRequest request);
        @Mapping(target = "productVariantId",source = "entity.productVariant.id")
        @Mapping(target = "colorName",source = "entity.productVariant.color.name")
        @Mapping(target = "colorHex",source = "entity.productVariant.color.hex")
        @Mapping(target = "sku",source = "entity.productVariant.color.hex")
        @Mapping(target = "unitPriceBefore",source = "entity.productVariant.color.hex")
        @Mapping(target = "unitPriceAtOrder",source = "entity.productVariant.color.hex")
        @Mapping(target = "lineTotal",source = "entity.productVariant.color.hex")



        OrderItemResponse toResponse(OrderItem entity);
    }
