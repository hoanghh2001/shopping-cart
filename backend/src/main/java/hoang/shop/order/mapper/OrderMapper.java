package hoang.shop.order.mapper;

import hoang.shop.config.MapStructConfig;
import hoang.shop.order.dto.request.OrderCreateRequest;
import hoang.shop.order.dto.request.OrderUpdateRequest;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = MapStructConfig.class)

public interface OrderMapper {
    Order toEntity(OrderCreateRequest request);
    void merger(@MappingTarget Order entity, OrderUpdateRequest request);
    OrderResponse toResponse(Order entity);
}
