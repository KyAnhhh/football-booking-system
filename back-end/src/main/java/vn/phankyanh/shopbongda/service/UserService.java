package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.dto.request.UserCreationRequest;
import vn.phankyanh.shopbongda.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.entity.Role;
import vn.phankyanh.shopbongda.entity.User;
import vn.phankyanh.shopbongda.exception.AppException;
import vn.phankyanh.shopbongda.exception.ErrorCode;
import vn.phankyanh.shopbongda.mapper.UserMapper;
import vn.phankyanh.shopbongda.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User handleCreateUsers(UserCreationRequest request){

        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        User user = userMapper.toUser(request);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10); // 10 là độ mạnh của password số càng lớn thì càng khó
        user.setPassword(passwordEncoder.encode(request.getPassword())); // lấy password user gửi lên và hash

        return this.userRepository.save(user);
    }

    public List<User> handleGetAllUsers(){
        return this.userRepository.findAll();
    }

    public User handleGetUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ID_INVALID));
    }

    public User hanleUpdateUser(Long id, UserUpdateRequest request){
        User updateUser = this.userRepository.findById(id)
                .orElseThrow(() ->new AppException(ErrorCode.ID_INVALID));
        if(!updateUser.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if(!updateUser.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        userMapper.updateUser(updateUser, request);

        return userRepository.save(updateUser);
    }

    public void handleDeleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new AppException(ErrorCode.ID_INVALID);
        }
         this.userRepository.deleteById(id);
    }

}
