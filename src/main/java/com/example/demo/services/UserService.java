package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    public boolean existByEmail(String email){
        return userRepository.existsByEmail(email);

    }
    public Optional<User> findUserByEmail(String email)
{
    return userRepository.findByEmail(email);
}
    public void saveUser(User user) {
        userRepository.save(user);
    }
    public  void updateUser(Long id, User user){
        user.setId(id);
        userRepository.save(user);
    }
    public void removeUserById(Long id){
        userRepository.deleteById(id);
    }
    public void changeUserRole(Long id, Role role)
    {
        User  user = userRepository.findById(id).orElse(null);
        if(user!= null){
            user.setRole(role);
            userRepository.save(user);
        }
    }
}
