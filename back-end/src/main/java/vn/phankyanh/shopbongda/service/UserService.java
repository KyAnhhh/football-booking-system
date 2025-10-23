package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import vn.phankyanh.shopbongda.entity.User;
import vn.phankyanh.shopbongda.exception.UserNotFoundException;
import vn.phankyanh.shopbongda.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User handleCreateUsers(User user){
        return this.userRepository.save(user);
    }

    public List<User> handleGetAllUsers(){
        return this.userRepository.findAll();
    }

    public User handleGetUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User hanleUpdateUser(Long id, User userDetails){
        User updateUser = this.userRepository.findById(id)
                .orElseThrow(() ->new UserNotFoundException(id));
        updateUser.setUsername(userDetails.getUsername());
        updateUser.setEmail(userDetails.getEmail());
        updateUser.setUsername(userDetails.getUsername());
        updateUser.setPhone(userDetails.getPhone());
        updateUser.setStatus(userDetails.getStatus());

        return userRepository.save(updateUser);
    }

    public void handleDeleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException(id);
        }
         this.userRepository.deleteById(id);
    }

}
