package com.martin.article.form;

import com.martin.article.model.Comment;
import lombok.Data;

import java.util.Date;

@Data
public class CommentForm {
    private String content;
    private Long replyToId;

    public Comment toComment(){
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPublishedDate(new Date());

        return comment;
    }
}
