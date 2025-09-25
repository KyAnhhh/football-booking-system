package vn.phankyanh.shopbongda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.phankyanh.shopbongda.domain.User;
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

    public List<User> handleGetAllUser(){
        return this.userRepository.findAll();
    }

    public Optional<User> handleGetUserById(Long id){
        return this.userRepository.findById(id);
    }

    public User handleUpdateUser(Long id, User userUpdate){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setUsername(userUpdate.getUsername());
            user.setEmail(userUpdate.getEmail());
            user.setPassword(userUpdate.getPassword());

            return userRepository.save(user);
        }
        return null;
    }
    
     public void handleDeleteUserById(Long id){
        this.userRepository.deleteById(id);
    }
}
