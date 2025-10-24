package vn.phankyanh.shopbongda.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.phankyanh.shopbongda.dto.request.UserCreationRequest;
import vn.phankyanh.shopbongda.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.entity.User;
import vn.phankyanh.shopbongda.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setData(userService.handleCreateUsers(request));
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<User>> getAllUser(){
        ApiResponse<List<User>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.userService.handleGetAllUsers());

        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable("id") Long id) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.userService.handleGetUserById(id));
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequest request){
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.userService.hanleUpdateUser(id, request));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable("id") Long id){
        this.userService.handleDeleteUser(id);

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Delete successfully with id: "+ id);

        return ResponseEntity.ok(apiResponse);
    }
}
