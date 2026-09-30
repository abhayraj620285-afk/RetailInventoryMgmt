package com.hcl.inventory.service;

import com.hcl.inventory.model.User;
import com.hcl.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public User ceateUser(User user){
       return  userRepository.save(user);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
   public User findById(Long id){
       return userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"+ id));
   }
   public void deleteById(Long id) {
       User temp = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User id not Existed" + id));
       userRepository.delete(temp);
   }
   public User updateById(Long id,User user){
       User temp = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User id not Existed" + id));
       temp.setUsername(user.getUsername());
       temp.setEmail(user.getEmail());
       temp.setRole(user.getRole());
       temp.setPassword(user.getPassword());
       return userRepository.save(temp);

   }
   }

