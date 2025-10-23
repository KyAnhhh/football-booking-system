package vn.phankyanh.shopbongda.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.phankyanh.shopbongda.entity.User;
import vn.phankyanh.shopbongda.service.UserService;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class UserController {

    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User userCreated = this.userService.handleCreateUsers(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        return ResponseEntity.ok(this.userService.handleGetAllUsers());
    }

    @GetMapping
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = this.userService.handleGetUserById(id);
        return ResponseEntity.ok(user);
    }
}
