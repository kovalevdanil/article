package com.martin.article.controller.advice;

import com.martin.article.exception.article.ArticleNotFoundException;
import com.martin.article.exception.article.ArticlePermissionDeniedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.annotation.Repeatable;

@ControllerAdvice
public class CustomAdvice {

    private static final String ARTICLE_NOT_FOUND = "article_not_found";
    private static final String ARTICLE_PERMISSION_DENIED = "article_permission_denied";

    @Data
    @AllArgsConstructor
    static class Response{
        private String code;
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<Response> handleArticleNotFound(Exception ex){
        return new ResponseEntity<>(new Response(ARTICLE_NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ArticlePermissionDeniedException.class)
    public ResponseEntity<Response> handlePermissionDenied(Exception ex){
        return new ResponseEntity<>(new Response(ARTICLE_PERMISSION_DENIED), HttpStatus.UNAUTHORIZED);
    }
}
