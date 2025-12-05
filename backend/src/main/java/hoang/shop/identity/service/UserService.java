package hoang.shop.identity.service;


import hoang.shop.identity.dto.request.UserCreateRequest;
import hoang.shop.identity.dto.request.UserUpdateRequest;
import hoang.shop.identity.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    //Create & Update
    UserResponse createUser(UserCreateRequest userCreateRequest);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest);

    // Read
    UserResponse findById(Long id);
    UserResponse findByEmail(String email);
    UserResponse findByPhone(String phone);
    // Exists
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    // List & search
    Page<UserResponse> listActiveUsers(Pageable pageable);
    Page<UserResponse> listDeletedUsers(Pageable pageable);
    Page<UserResponse> searchUsers(String keyword, Pageable pageable);
    // Delete
    boolean softDeleteById(Long id);

}
