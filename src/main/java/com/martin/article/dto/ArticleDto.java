package com.martin.article.dto;

import com.martin.article.model.Article;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ArticleDto {
    private Long id;
    private String slug;
    private String title;
    private String content;
    private Date publicationDate;

    private List<TagDto> tags;
    private Integer likes;
    private Integer dislikes;

    public ArticleDto(Article article){
        id = article.getId();
        slug = article.getSlug();
        title = article.getTitle();
        content = article.getContent();
        publicationDate = article.getPublicationDate();

        tags = article.getTags() == null ? null : article.getTags().stream().map(TagDto::new).collect(Collectors.toList());
        likes = article.getUsersLiked() == null ? null : article.getUsersLiked().size();
        dislikes = article.getUsersDisliked() == null ? null : article.getUsersDisliked().size();
    }

}
