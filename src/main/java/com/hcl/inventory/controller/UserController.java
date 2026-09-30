package com.hcl.inventory.controller;

import com.hcl.inventory.model.User;
import com.hcl.inventory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private UserService userService;
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    public User findById(@PathVariable Long id){
        return userService.findById(id);
    }
    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        return "User deleted successfully";
    }
    @PutMapping("/{id}")
    public User updateById( @PathVariable Long id, @RequestBody User user){
        return userService.updateById(id, user);
    }

}
