package vn.phankyanh.shopbongda.user.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.exception.UserNotFoundException;
import vn.phankyanh.shopbongda.user.domain.Role;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.dto.request.UpdateRoleRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.user.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.user.mapper.UserMapper;
import vn.phankyanh.shopbongda.user.repository.UserRepository;
import vn.phankyanh.shopbongda.user.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserCreateResponse createUser(UserCreateRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = (request.getRole() != null)
                ? request.getRole()
                : Role.ROLE_CLIENT;

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return UserCreateResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    @Override
    public Page<UserCreateResponse> getAllUsers(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<User> usersPage;

        if (search != null && !search.trim().isEmpty()) {

            usersPage = userRepository.findByEmailContaining(
                    search,
                    pageable
            );

        } else {

            usersPage = userRepository.findAll(pageable);
        }

        return usersPage.map(userMapper::toUserCreateResponse);
    }

    @Override
    public UserCreateResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toUserCreateResponse(user);
    }

    @Override
    public UserCreateResponse updateUser(
            Long id,
            UserUpdateRequest request
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.getUsername() != null
                && !request.getUsername().isBlank()) {

            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null
                && !request.getEmail().isBlank()) {

            if (!request.getEmail().equals(user.getEmail())) {

                if (userRepository.existsByEmail(request.getEmail())) {
                    throw new RuntimeException("Email đã tồn tại!");
                }
            }

            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null
                && !request.getPhone().isBlank()) {

            user.setPhone(request.getPhone());
        }

        return userMapper.toUserCreateResponse(
                userRepository.save(user)
        );
    }

    @Override
    public User updateRole(Long id, UpdateRoleRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setRole(request.getRole());

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }
}