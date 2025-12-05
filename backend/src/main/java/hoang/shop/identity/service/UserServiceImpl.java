package hoang.shop.identity.service;

import hoang.shop.identity.dto.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import hoang.shop.common.exception.DuplicateResourceException;
import hoang.shop.common.exception.NotFoundException;
import hoang.shop.identity.dto.request.UserUpdateRequest;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.mapper.UserMapper;
import hoang.shop.identity.model.User;
import hoang.shop.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // create
    @Override
    public UserResponse createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email()))
            throw new DuplicateResourceException("{error.user.email.exists}");
        User user = userMapper.toEntity(registerRequest);
        user = userRepository.saveAndFlush(user);
        return userMapper.toResponse(user);
    }

    // update
    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("{error.user.id.notFound}"));
        userMapper.updateEntityFromDto(userUpdateRequest, user);
        return userMapper.toResponse(userRepository.save(user));
    }


    // read (find)
    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("{error.user.id.notFound}"));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("{error.user.email.notFound}"));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse findByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException("{error.user.phone.notFound}"));
        return userMapper.toResponse(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> listActiveUsers(Pageable pageable) {
        return userRepository.findByDeletedFalse(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> listDeletedUsers(Pageable pageable) {
        return userRepository.findByDeletedTrue(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchByKeyword(keyword, pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    // delete
    @Override
    public boolean softDeleteById(Long id) {
        return userRepository.softDeleteById(id) > 0;
    }
}

