package com.martin.article.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class LoginForm {
    private String username;
    private String password;
}
