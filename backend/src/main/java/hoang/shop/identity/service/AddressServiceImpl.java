package hoang.shop.identity.service;

import hoang.shop.identity.model.User;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepo;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    @Override
    public AddressResponse getById(Long addressId) {
        Address a = addressRepo.findById(addressId)
                .orElseThrow(() -> new NotFoundException("{error.address.id.not-found}"));
        return addressMapper.toResponse(a);
    }

    @Override
    public List<AddressResponse> listByUser(Long userId) {
        return addressRepo.findAllByUserId(userId).stream()
                .map(addressMapper::toResponse).toList();
    }

    @Override
    public boolean softDelete(Long addressId) {
        return addressRepo.softDeleteById(addressId) > 0;
    }

    @Override
    public boolean restore(Long addressId) {
        return addressRepo.restoreById(addressId) > 0;
    }

    @Override
    public boolean hardDelete(Long addressId) {
        if (!addressRepo.existsById(addressId)) return false;
        addressRepo.deleteById(addressId);
        return true;
    }

    @Override
    public AddressResponse create(Long userId, AddressCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{error.user.id.not-found}"));
        Address address = addressMapper.toEntity(request);
        if (addressRepo.findAllByUserId(userId).isEmpty()){
            address.setDefault(true);
        }
        List<Address> addresses = user.getAddress();
        List<Address> listAddressMain =
        addresses.stream().filter(i->i.isDefault()== true).toList();
        if (listAddressMain.isEmpty())
            address.setDefault(true);
        String addressLine1 = address.getStreetNumber();
        String[] parts = addressLine1.split("-");
        address.setChome(Integer.valueOf(parts[0]));
        address.setBan(Integer.valueOf(parts[1]));
        address.setGo(Integer.valueOf(parts[2]));
        address.setUser(user);
        StringBuilder sb = new StringBuilder();
        if (address.getPrefecture() != null) sb.append(address.getPrefecture());
        if (address.getMunicipality() != null) sb.append(address.getMunicipality());
        if (address.getStreetNumber() != null) sb.append(address.getStreetNumber());
        if (address.getBuilding() != null) sb.append(address.getBuilding());
        if (address.getRoomNumber() != null) sb.append(" ").append(address.getRoomNumber());
        address.setFullAddress(sb.toString());
        address = addressRepo.save(address);
        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse getById(Long userId, Long addressId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{error.user.id.not-found}"));
        Address address = addressRepo.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new NotFoundException("{error.address.id.not-found}"));
        String addressLine1 = address.getStreetNumber();
        String[] parts = addressLine1.split("-");
        address.setChome(Integer.valueOf(parts[0]));
        address.setBan(Integer.valueOf(parts[1]));
        address.setGo(Integer.valueOf(parts[2]));
        StringBuilder sb = new StringBuilder();
        if (address.getPrefecture() != null) sb.append(address.getPrefecture());
        if (address.getMunicipality() != null) sb.append(address.getMunicipality());
        if (address.getStreetNumber() != null) sb.append(address.getStreetNumber());
        sb.append(" ");
        if (address.getBuilding() != null) sb.append(address.getBuilding());
        if (address.getRoomNumber() != null) sb.append(" ").append(address.getRoomNumber());
        address.setFullAddress(sb.toString());
        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse update(Long userId, Long addressId, AddressUpdateRequest request) {
        Address a = addressRepo.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new NotFoundException(""));
        addressMapper.updateEntityFromDto(request, a);
        return addressMapper.toResponse(a);
    }

    @Override
    public boolean softDelete(Long userId, Long addressId) {
        return addressRepo.softDeleteByUser(addressId, userId) > 0;
    }

    @Override
    public boolean restore(Long userId, Long addressId) {
        return addressRepo.restoreByUser(addressId, userId) > 0;
    }

    @Override
    public Page<AddressResponse> list(Long userId, Pageable pageable) {
        return addressRepo.findAllActiveByUser(userId, pageable)
                .map(addressMapper::toResponse);
    }

    @Override
    public boolean setDefault(Long userId, Long addressId) {
        if (addressRepo.unsetDefault(userId) > 0) {
            return addressRepo.setDefault(userId, addressId) > 0;
        }
        return false;
    }

    @Override
    public AddressResponse getDefault(Long userId) {
        Address a = addressRepo.findDefaultAddressByUserId(userId)
                .orElseThrow(() -> new NotFoundException(""));
        return addressMapper.toResponse(a);
    }

}
