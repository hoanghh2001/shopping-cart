package hoang.shop.identity.service;

import lombok.RequiredArgsConstructor;
import hoang.shop.common.exception.NotFoundException;
import hoang.shop.identity.dto.request.AddressCreateRequest;
import hoang.shop.identity.dto.request.AddressUpdateRequest;
import hoang.shop.identity.dto.response.AddressResponse;
import hoang.shop.identity.mapper.AddressMapper;
import hoang.shop.identity.model.Address;
import hoang.shop.identity.repository.AddressRepository;
import hoang.shop.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepo;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;


    @Override
    public AddressResponse createAddress(Long userId, AddressCreateRequest addressCreateRequest) {
        var userRef = userRepository.getReferenceById(userId);
        var address = addressMapper.toEntity(addressCreateRequest);
        address.setUser(userRef);
        var saved = addressRepo.saveAndFlush(address);
        return addressMapper.toResponse(saved);
    }

    @Override
    public AddressResponse getById(Long id) {
        Address address = addressRepo.findById(id)
                .orElseThrow(()-> new NotFoundException(""));
        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse updateAddress(Long addressId,AddressUpdateRequest addressUpdateRequest) {
        Address address = addressRepo.findById(addressId).orElseThrow(()->new NotFoundException(""));
        addressMapper.updateEntityFromDto(addressUpdateRequest,address);
        return addressMapper.toResponse(address);
    }

    @Override
    public boolean restoreById(Long id) {
        return addressRepo.restoreById(id)>0;
    }

    @Override
    public boolean softDeleteById(Long id) {
        return addressRepo.softDeleteById(id)>0;
    }

    @Override
    public Page<AddressResponse> listByUser(Long userId, Pageable pageable) {
        return addressRepo.findAllByIdAndDeletedFalse(userId,pageable).map(addressMapper::toResponse);
    }

    @Override
    public boolean setDefault(Long userId, Long address) {
        if (addressRepo.unsetDefault(userId)>0)
            return addressRepo.setDefault(userId,address)>0;
        return false;
    }

    @Override
    public AddressResponse getDefault(Long userId) {
        Address address = addressRepo.findDefaultAddressById(userId).orElseThrow(()->new NotFoundException(""));
        return addressMapper.toResponse(address);
    }
}
