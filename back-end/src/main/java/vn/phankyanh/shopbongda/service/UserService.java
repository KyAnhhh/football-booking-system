package vn.phankyanh.shopbongda.service;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Role;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.exception.UserNotFoundException;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreateResponse handleCreateUser(UserCreateRequest request){
    if(userRepository.existsByEmail(request.getEmail()))
        throw new RuntimeException("Email already exists");

        // sửa
    Role role = (request.getRole() != null) ? request.getRole() : Role.ROLE_CLIENT;
//        Role role = (request.getRole() != null) ? request.getRole() : Role.client;
    User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .role(request.getRole())
            .status(request.getStatus())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    this.userRepository.save(user);
    return UserCreateResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .password(user.getPassword())
            .phone(user.getPhone())
            .role(user.getRole())
            .status(user.getStatus())
            .message("User created successfully!")
            .build();
    }

    public List<User> handleGetAllUsers(){
        return this.userRepository.findAll();
    }

    public User handleGetUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User handleUpdateUser(Long id, UserUpdateRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());

        return userRepository.save(user);
    }
    
    public void handleDeleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
