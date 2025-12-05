package hoang.shop.identity.mapper;

import hoang.shop.identity.dto.request.UserCreateRequest;
import hoang.shop.identity.dto.request.UserUpdateRequest;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.model.User;
import org.mapstruct.*;
// componentModel : kiểu đại diện dữ liêụ thành phần của ai?? Spring
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserCreateRequest dto);
    //nullValuePropertyMappingStrategy : Chiến lược ánh xạ khi gặp thuộc tính có giá trị null
    //IGNORE option này bỏ qua giá trị null
    @BeanMapping(nullValuePropertyMappingStrategy  = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserUpdateRequest dto , @MappingTarget User entity);

    UserResponse toResponse(User entity);

}
