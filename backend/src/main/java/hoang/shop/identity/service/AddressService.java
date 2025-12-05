package hoang.shop.identity.service;

import hoang.shop.identity.dto.request.AddressCreateRequest;
import hoang.shop.identity.dto.request.AddressUpdateRequest;
import hoang.shop.identity.dto.response.AddressResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AddressService {
    //create
    AddressResponse createAddress(Long id ,AddressCreateRequest addressCreateRequest);
    //read
    AddressResponse getById(Long addressId);

    //update
    AddressResponse updateAddress(Long addressId,AddressUpdateRequest addressUpdateRequest);
    boolean restoreById(Long addressId);
    //delete
    boolean softDeleteById(Long addressId);
    //list
    Page<AddressResponse> listByUser(Long userId, Pageable pageable);
    //default address
    boolean setDefault(Long userId,Long address);
    AddressResponse getDefault(Long userId);


}
