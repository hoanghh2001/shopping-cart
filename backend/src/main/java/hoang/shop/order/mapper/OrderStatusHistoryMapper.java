package hoang.shop.order.mapper;

import hoang.shop.config.MapStructConfig;
import hoang.shop.order.dto.request.OrderStatusHistoryCreateRequest;
import hoang.shop.order.dto.response.OrderStatusHistoryResponse;
import hoang.shop.order.model.OrderStatusHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface OrderStatusHistoryMapper {
    OrderStatusHistory toEntity(OrderStatusHistoryCreateRequest request);
    OrderStatusHistoryResponse toResponse(OrderStatusHistory entity);

}
