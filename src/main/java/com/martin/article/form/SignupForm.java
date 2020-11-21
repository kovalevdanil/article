package com.martin.article.form;

import com.martin.article.exception.PasswordsDontMatchException;
import com.martin.article.model.User;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class SignupForm {
    private String username;
    private String password1;
    private String password2;

    public User toUser(PasswordEncoder encoder) throws PasswordsDontMatchException{

        if (!password1.equals(password2))
            throw new PasswordsDontMatchException();

        User user = new User();
        user.setPassword(encoder.encode(password1));
        user.setUsername(username);

        return user;
    }
}
