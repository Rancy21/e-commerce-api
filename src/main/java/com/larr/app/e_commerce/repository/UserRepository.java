package com.larr.app.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.larr.app.e_commerce.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findUserByEmail(String email);

    @Query("From User u where u.isActive = true")
    List<User> findAll();
}
