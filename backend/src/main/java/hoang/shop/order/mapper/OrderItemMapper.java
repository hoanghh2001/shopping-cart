package hoang.shop.order.mapper;

import hoang.shop.config.MapStructConfig;
import hoang.shop.order.dto.request.OderItemUpdateRequest;
import hoang.shop.order.dto.request.OrderItemCreateRequest;
import hoang.shop.order.dto.response.OrderItemResponse;
import hoang.shop.order.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = MapStructConfig.class)

public interface OrderItemMapper {
    OrderItem toEntity(OrderItemCreateRequest request);
    void merger(@MappingTarget OrderItem entity, OderItemUpdateRequest request);
    OrderItemResponse toResponse(OrderItem entity);
}
