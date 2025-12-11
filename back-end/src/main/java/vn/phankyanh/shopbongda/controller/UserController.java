package vn.phankyanh.shopbongda.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Nhận dữ liệu JSON gửi từ client
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.UpdateRoleRequest;
import vn.phankyanh.shopbongda.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.service.UserService;

@RestController // kết hợp @Controller + @ResponBody
@RequiredArgsConstructor // Tự động sinh ra constructor cho các filed final or đánh dấu @NonNull
@RequestMapping("/api/admin/users")
public class UserController {
    
    private final UserService userService;

    @PostMapping
    public ApiResponse<UserCreateResponse> createUser(@RequestBody UserCreateRequest userCreateRequest ){
        ApiResponse<UserCreateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(userService.handleCreateUser(userCreateRequest));
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<Page<UserCreateResponse>> getAllUsers(@RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(required = false) String search) {
        ApiResponse<Page<UserCreateResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(
            userService.handleGetAllUsers(pageNo, pageSize, sortBy, sortDir, search)
    );
    return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<UserCreateResponse> getUserById(@PathVariable("id") Long id) {
        ApiResponse<UserCreateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.userService.handleGetUserById(id));
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
         apiResponse.setData(this.userService.handleUpdateUser(id, request));
        return apiResponse;
    }

    @PutMapping("/{id}/role")
    public ApiResponse<User> updateRole(@PathVariable("id") Long id, @RequestBody UpdateRoleRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
         apiResponse.setData(this.userService.handleUpdateRole(id, request));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUserById(@PathVariable("id") Long id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.userService.handleDeleteUserById(id);
        apiResponse.setMessage("Delete user successful with id: " + id);
        return apiResponse;
    }
}
