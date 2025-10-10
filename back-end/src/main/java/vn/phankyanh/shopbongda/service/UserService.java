package vn.phankyanh.shopbongda.service;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.UserCreateRequest;
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

    User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    this.userRepository.save(user);
    return UserCreateResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
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

    public User handleUpdateUser(Long id, User userUpdate){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setUsername(userUpdate.getUsername());
        user.setEmail(userUpdate.getEmail());
        user.setPassword(userUpdate.getPassword());

        return userRepository.save(user);
    }
    
    public void handleDeleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
