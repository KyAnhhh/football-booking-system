package vn.phankyanh.shopbongda.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Nhận dữ liệu JSON gửi từ client
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.dto.request.UpdateRoleRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.user.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.user.service.UserService;

@RestController // kết hợp @Controller + @ResponBody
@RequiredArgsConstructor // Tự động sinh ra constructor cho các filed final or đánh dấu @NonNull
public class UserController {
    
    private final UserService userService;

    @PostMapping("/api/admin/users")
    public ApiResponse<UserCreateResponse> createUser(@RequestBody UserCreateRequest userCreateRequest ){
        ApiResponse<UserCreateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(userService.createUser(userCreateRequest));
        apiResponse.setMessage("Created user successfully");
        return apiResponse;
    }

    @GetMapping("/api/admin/users")
    public ApiResponse<Page<UserCreateResponse>> getAllUsers(@RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(required = false) String search) {
        ApiResponse<Page<UserCreateResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(
            userService.getAllUsers(pageNo, pageSize, sortBy, sortDir, search)
        );
        apiResponse.setMessage("Get all users successfully");
        return apiResponse;
    }

    @GetMapping("/api/admin/users/{id}")
    public ApiResponse<UserCreateResponse> getUserById(@PathVariable("id") Long id) {
        ApiResponse<UserCreateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.userService.getUserById(id));
        apiResponse.setMessage("Get user by id successfully");
        return apiResponse;
    }

    @PutMapping("/api/admin/users/{id}")
    public ApiResponse<UserCreateResponse> updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequest request) {
        ApiResponse<UserCreateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.userService.updateUser(id, request));
        apiResponse.setMessage("Updated user successfully");
        return apiResponse;
    }

    @PutMapping("/api/admin/users/{id}/role")
    public ApiResponse<User> updateRole(@PathVariable("id") Long id, @RequestBody UpdateRoleRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.userService.updateRole(id, request));
        apiResponse.setMessage("Updated user role successfully");
        return apiResponse;
    }

    @DeleteMapping("/api/admin/users/{id}")
    public ApiResponse<String> deleteUserById(@PathVariable("id") Long id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.userService.deleteUserById(id);
        apiResponse.setMessage("Delete user successful with id: " + id);
        return apiResponse;
    }

    @PutMapping("/api/public/users/profile/update")
    public ApiResponse<UserCreateResponse> updateProfile(@RequestBody UserUpdateRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<UserCreateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(userService.updateUser(userId, request));
        apiResponse.setMessage("Updated user profile successfully");
        return apiResponse;
    }

        @GetMapping("/api/public/users/profile")
        public ApiResponse<UserCreateResponse> getMe(@AuthenticationPrincipal Jwt jwt){
            Long userId = jwt.getClaim("id");
            ApiResponse<UserCreateResponse> apiResponse = new ApiResponse<>();
            apiResponse.setData(userService.getUserById(userId));
            apiResponse.setMessage("Get user profile successfully");
            return apiResponse;
        }
}
