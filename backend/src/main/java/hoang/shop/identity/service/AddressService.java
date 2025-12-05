package hoang.shop.identity.service;

import hoang.shop.identity.dto.request.AddressCreateRequest;
import hoang.shop.identity.dto.request.AddressUpdateRequest;
import hoang.shop.identity.dto.response.AddressResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AddressService {
    //Admin
    AddressResponse getById(Long addressId);

    List<AddressResponse> listByUser(Long userId);

    boolean softDelete(Long addressId);

    boolean restore(Long addressId);

    boolean hardDelete(Long addressId);


    //user
    AddressResponse create(Long userId, AddressCreateRequest request);

    AddressResponse getById(Long userId, Long addressId);

    AddressResponse update(Long userId, Long addressId, AddressUpdateRequest request);

    boolean softDelete(Long userId, Long addressId);

    boolean restore(Long userId, Long addressId);

    Page<AddressResponse> list(Long userId, Pageable pageable);

    boolean setDefault(Long userId, Long addressId);

    AddressResponse getDefault(Long userId);

}
