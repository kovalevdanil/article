package com.martin.article.service;

import com.martin.article.exception.user.UsernameAlreadyExistsException;
import com.martin.article.model.User;

public interface UserService {
    User findUserById(Long id);
    User findUserByUsername(String username);
    Iterable<User> findAll();
    Iterable<User> findAll(Integer page, Integer size);
    User save(User user) throws UsernameAlreadyExistsException;
}
