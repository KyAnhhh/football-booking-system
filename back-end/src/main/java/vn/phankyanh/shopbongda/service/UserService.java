package vn.phankyanh.shopbongda.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Role;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.UpdateRoleRequest;
import vn.phankyanh.shopbongda.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.exception.UserNotFoundException;
import vn.phankyanh.shopbongda.mapper.UserMapper;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserCreateResponse handleCreateUser(UserCreateRequest request){
    if(userRepository.existsByEmail(request.getEmail()))
        throw new RuntimeException("Email already exists");

    Role role = (request.getRole() != null) ? request.getRole() : Role.ROLE_CLIENT;
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
            // .password(user.getPassword())
            .phone(user.getPhone())
            .role(user.getRole())
            .status(user.getStatus())
            .build();
    }

    public Page<UserCreateResponse> handleGetAllUsers(int pageNo, int pageSize, String sortBy, String sortDir, String search){
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<User> usersPage;

        if (search != null && !search.trim().isEmpty()) {
            usersPage = userRepository.findByEmailContaining(search, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        return usersPage.map(userMapper::toUserCreateResponse);
    }

    public UserCreateResponse handleGetUserById(Long id){
        User user= userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        // k cần set từng giá trị thì ta sử dụng mapper
        return userMapper.toUserCreateResponse(user);
    }

    public UserCreateResponse handleUpdateUser(Long id, UserUpdateRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Chỉ update nếu có truyền lên
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if(!request.getEmail().equals(user.getEmail())){
                    // Check email tồn tại
                if (userRepository.existsByEmail(request.getEmail())) {
                    throw new RuntimeException("Email đã tồn tại!");
                }
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user.setPhone(request.getPhone());
        }

        return userMapper.toUserCreateResponse(userRepository.save(user));
    }

    public User handleUpdateRole(Long id, UpdateRoleRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setRole(request.getRole());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    
    public void handleDeleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
