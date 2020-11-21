package com.martin.article.controller;

import com.martin.article.dto.UserDto;
import com.martin.article.exception.PasswordsDontMatchException;
import com.martin.article.exception.UsernameAlreadyExistsException;
import com.martin.article.form.SignupForm;
import com.martin.article.model.User;
import com.martin.article.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users", produces = {"application/json"})
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<UserDto> postUser(@RequestBody SignupForm form) throws PasswordsDontMatchException, UsernameAlreadyExistsException {
        User user = form.toUser(passwordEncoder);
        userService.save(user);

        return new ResponseEntity<>(new UserDto(user), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable(name = "id") Long id){
        User user = userService.findUserById(id);
        if (user == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new UserDto(user));
    }
}
