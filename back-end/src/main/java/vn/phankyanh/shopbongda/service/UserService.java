package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import vn.phankyanh.shopbongda.entity.User;
import vn.phankyanh.shopbongda.exception.UserNotFoundException;
import vn.phankyanh.shopbongda.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private UserRepository userRepository;

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

}
