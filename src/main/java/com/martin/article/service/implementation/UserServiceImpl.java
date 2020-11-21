package com.martin.article.service.implementation;

import com.martin.article.exception.UsernameAlreadyExistsException;
import com.martin.article.model.User;
import com.martin.article.repository.UserRepository;
import com.martin.article.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Iterable<User> findAll(Integer page, Integer size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public User save(User user) {
        User userWithUsername = findUserByUsername(user.getUsername());
        if (userWithUsername != null)
            throw new UsernameAlreadyExistsException();
        return userRepository.save(user);
    }
}
