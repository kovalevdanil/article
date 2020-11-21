package com.martin.article.dto;

import com.martin.article.model.Comment;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private List<CommentDto> replies;
    private Date publishedDate;
    private Integer likes;
    private Integer dislikes;
    private String authorUsername;

    public CommentDto(Comment comment, int nesting){
        id = comment.getId();
        content = comment.getContent();
        publishedDate = comment.getPublishedDate();
        likes = comment.getUsersLiked() == null ? 0 : comment.getUsersLiked().size();
        dislikes = comment.getUsersDisliked() == null ? 0 : comment.getUsersDisliked().size();
        authorUsername = comment.getAuthor().getUsername();

        if (nesting > 0 && comment.getReplies() != null) {
            replies = comment.getReplies().stream().map(c -> new CommentDto(c, nesting - 1)).collect(Collectors.toList());
        }
    }

}
