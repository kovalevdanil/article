package com.martin.article.exception.handler;

import com.martin.article.exception.article.*;
import com.martin.article.exception.tag.TagNotFoundException;
import com.martin.article.exception.user.UsernameAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@ControllerAdvice
public class CustomAdvice {

    private static final String ARTICLE_NOT_FOUND = "article_not_found";
    private static final String ARTICLE_PERMISSION_DENIED = "article_permission_denied";
    private static final String DISLIKE_IS_SET = "dislike_is_set";
    private static final String LIKE_IS_SET = "like_is_set";
    private static final String ARTICLE_INVALID_LIKE_ACTION = "article_invalid_like_action";
    private static final String ARTICLE_INVALID_DISLIKE_ACTION = "article_invalid_dislike_action";

    private static final String TYPE_MISMATCH = "type_mismatch";

    public static final String TAG_NOT_FOUND = "tag_not_found";
    private static final String USER_USERNAME_EXISTS = "user_username_exists" ;

    @Data
    @AllArgsConstructor
    static class Response{
        private String code;
    }

    @Data
    @AllArgsConstructor
    static class ResponseWithMessage {
        private String code;
        private String message;
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<Response> handleArticleNotFound(Exception ex){
        return new ResponseEntity<>(new Response(ARTICLE_NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ArticlePermissionDeniedException.class)
    public ResponseEntity<Response> handleArticlePermissionDenied(Exception ex){
        return new ResponseEntity<>(new Response(ARTICLE_PERMISSION_DENIED), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<Response> handleTagNotFound(Exception ex){
        return new ResponseEntity<>(new Response(TAG_NOT_FOUND), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DislikeIsSetException.class)
    public ResponseEntity<Response> handleDislikeIsSet(Exception ex){
        return constructResponse(DISLIKE_IS_SET, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LikeIsSetException.class)
    public ResponseEntity<Response> handleLikeIsSet(Exception ex){
        return constructResponse(LIKE_IS_SET, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidLikeActionException.class)
    public ResponseEntity<Response> handleArticleInvalidLikeAction(Exception ex){
        return constructResponse(ARTICLE_INVALID_LIKE_ACTION, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDislikeActionException.class)
    public ResponseEntity<Response> handleArticleInvalidDislikeAction(Exception ex){
        return constructResponse(ARTICLE_INVALID_DISLIKE_ACTION, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseWithMessage> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
        Class<?> type = ex.getRequiredType();
        String message;
        assert type != null;
        if(type.isEnum()){
            message = "The parameter " + ex.getName() + " must have a value among : " + Arrays.toString(type.getEnumConstants());
        }
        else{
            message = "The parameter " + ex.getName() + " must be of type " + type.getTypeName();
        }
        return new ResponseEntity<>(new ResponseWithMessage(TYPE_MISMATCH, message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Response> handleUsernameExists(UsernameAlreadyExistsException ex){
        return constructResponse(USER_USERNAME_EXISTS, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Response> constructResponse(String code, HttpStatus status){
        return new ResponseEntity<>(new Response(LIKE_IS_SET), status);
    }

}
