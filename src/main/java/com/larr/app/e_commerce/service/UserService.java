package com.larr.app.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User saveUser(User user) {
        if (repository.findUserByEmail(user.getEmail()).isPresent()) {
            return null;
        }

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        return repository.save(user);
    }

    public Optional<User> getUser(String email) {
        return repository.findUserByEmail(email);
    }

    public User getUserById(String id) {
        return repository.findById(id).isPresent() ? repository.findById(id).get() : null;
    }

    public User deleteUser(User user) {
        user.setActive(false);
        return repository.save(user);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
