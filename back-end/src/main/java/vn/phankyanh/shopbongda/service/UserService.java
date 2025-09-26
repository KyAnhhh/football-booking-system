package vn.phankyanh.shopbongda.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.exception.UserNotFoundException;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository; 

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user){
        return this.userRepository.save(user);
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
