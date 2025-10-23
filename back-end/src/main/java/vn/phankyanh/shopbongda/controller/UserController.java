package vn.phankyanh.shopbongda.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.phankyanh.shopbongda.entity.User;
import vn.phankyanh.shopbongda.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User userCreated = userService.handleCreateUsers(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }


    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        return ResponseEntity.ok(this.userService.handleGetAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = this.userService.handleGetUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User userDetails){
        User updateUser = this.userService.hanleUpdateUser(id, userDetails);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(Long id){
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok("Delete successfully with id: "+ id);
    }
}
