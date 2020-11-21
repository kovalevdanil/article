package com.martin.article.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "user with this username already exists")
public class UsernameAlreadyExistsException extends RuntimeException{
}
