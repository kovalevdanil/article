package com.martin.article.service;

import com.martin.article.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    User findUserById(Long id);
    User findUserByUsername(String username);
    Iterable<User> findAll();
    Iterable<User> findAll(Integer page, Integer size);
    User save(User user);
}
