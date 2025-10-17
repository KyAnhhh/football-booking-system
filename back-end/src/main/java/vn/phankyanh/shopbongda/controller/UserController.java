package vn.phankyanh.shopbongda.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; // hash password
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor; // Nhận dữ liệu JSON gửi từ client
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.service.UserService;

@RestController // kết hợp @Controller + @ResponBody
@RequiredArgsConstructor // Tự động sinh ra constructor cho các filed final or đánh dấu @NonNull
public class UserController {
    
    private final UserService userService;

    @PostMapping("/users")
    public UserCreateResponse createUser(@RequestBody UserCreateRequest userCreateRequest ){
        return userService.handleCreateUser(userCreateRequest);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(this.userService.handleGetAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = this.userService.handleGetUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User userDetails) {
        User updatedUser = this.userService.handleUpdateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long id) {
        this.userService.handleDeleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
