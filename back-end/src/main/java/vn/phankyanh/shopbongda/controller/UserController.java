package vn.phankyanh.shopbongda.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor; // Nhận dữ liệu JSON gửi từ client
import vn.phankyanh.shopbongda.domain.User;
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
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(this.userService.handleGetAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = this.userService.handleGetUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
         apiResponse.setData(this.userService.handleUpdateUser(id, request));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long id) {
        this.userService.handleDeleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
