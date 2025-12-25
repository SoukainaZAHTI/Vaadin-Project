package com.example.vaadinproject.services;


import com.example.vaadinproject.entities.User;
import com.example.vaadinproject.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers(String filterText) {

        if(filterText == null || filterText.isEmpty()) {
            return userRepository.findAll();

        }  else {
            return userRepository.search(filterText);
        }

    }

    public long countUsers() {
        return userRepository.count();
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

}