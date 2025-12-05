package hoang.shop.identity.service;

import lombok.*;
import hoang.shop.common.exception.NotFoundException;
import hoang.shop.identity.dto.response.RoleResponse;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.dto.response.UserRoleResponse;
import hoang.shop.identity.mapper.RoleMapper;
import hoang.shop.identity.mapper.UserMapper;
import hoang.shop.identity.mapper.UserRoleMapper;
import hoang.shop.identity.model.Role;
import hoang.shop.identity.model.User;
import hoang.shop.identity.model.UserRole;
import hoang.shop.identity.model.UserRoleId;
import hoang.shop.identity.repository.RoleRepository;
import hoang.shop.identity.repository.UserRepository;
import hoang.shop.identity.repository.UserRoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepo;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Page<UserRoleResponse> listUserRole(Pageable pageable) {
        return userRoleRepo.findAll(pageable).map(userRoleMapper::toResponse);
    }

    @Override
    public boolean assignRoleToUser(Long userId, Long roleId,Long actorId ) {
        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("{error.user.notFound}"));
        Role role = roleRepository.findById(roleId).orElseThrow(()-> new NotFoundException("{error.role.notFound}"));
        if (userRoleRepo.existsByUser_IdAndRole_IdAndDeletedFalse(userId,roleId)) return false;
        try {
            UserRole ur = UserRole.builder()
                    .user(user)
                    .role(role)
                    .assignedBy(actorId).build();
            userRoleRepo.save(ur);
            return true;
        }catch (DataIntegrityViolationException e){
            return false;
        }
    }

    @Override
    public boolean removeRoleFromUser(Long userId, Long roleId,Long actorId) {
        return userRoleRepo.findById(new UserRoleId(userId,roleId))
                .filter(ur -> !ur.isDeleted())
                .map(ur -> {
                    ur.setDeleted(true);
                    ur.setRemovedBy(actorId);
                    ur.setRemovedAt(Instant.now());
                    return true;
                }).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleResponse> listRolesOfUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("{error.user.notFound}");
        }
        return userRoleRepo.findRolesByUserIdAndDeletedFalse(userId,pageable).map(roleMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> listUserOfRole(Long roleId, Pageable pageable) {
        if (!roleRepository.existsById(roleId)){
            throw new NotFoundException("{error.role.notFound}");
        }
        return userRoleRepo.findUsersByRoleIdAndDeletedFalse(roleId,pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasRole(Long userId, Long roleId) {
        return userRoleRepo.existsByUser_IdAndRole_IdAndDeletedFalse(userId,roleId);
    }
}
