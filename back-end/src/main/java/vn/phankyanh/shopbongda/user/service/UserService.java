package vn.phankyanh.shopbongda.user.service;

import org.springframework.data.domain.Page;

import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.dto.request.UpdateRoleRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.user.dto.response.UserCreateResponse;

public interface UserService {

    UserCreateResponse createUser(UserCreateRequest request);

    Page<UserCreateResponse> getAllUsers(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search
    );

    UserCreateResponse getUserById(Long id);

    UserCreateResponse updateUser(Long id, UserUpdateRequest request);

    User updateRole(Long id, UpdateRoleRequest request);

    void deleteUserById(Long id);
}