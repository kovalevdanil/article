package com.martin.article.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "passwords don't match")
public class PasswordsDontMatchException extends RuntimeException{

}
