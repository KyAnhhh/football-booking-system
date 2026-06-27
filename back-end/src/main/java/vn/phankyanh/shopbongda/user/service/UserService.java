package vn.phankyanh.shopbongda.user.service;

import org.springframework.data.domain.Page;

import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.domain.Role;
import vn.phankyanh.shopbongda.user.dto.request.UpdateRoleRequest;
import vn.phankyanh.shopbongda.user.dto.request.UpdateUserStatusRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.user.dto.response.UserCreateResponse;

public interface UserService {

    UserCreateResponse createUser(UserCreateRequest request);

    UserCreateResponse createUser(UserCreateRequest request, Long actorId);

    Page<UserCreateResponse> getAllUsers(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search,
            Role role,
            String status,
            Long actorId
    );

    UserCreateResponse getUserById(Long id, Long actorId);

    UserCreateResponse updateUser(Long id, UserUpdateRequest request, Long actorId);

    User updateRole(Long id, UpdateRoleRequest request, Long actorId);

    User updateStatus(Long id, UpdateUserStatusRequest request, Long actorId);

    void deleteUserById(Long id, Long actorId);
}
