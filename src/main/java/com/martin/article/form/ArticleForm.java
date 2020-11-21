package com.martin.article.form;

import com.martin.article.model.Article;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class ArticleForm {
    @Size(min = 5, max = 255)
    private String title;

    @NotNull
    private String content;

    public Article toArticle() {
        Article article = new Article();
        article.setTitle(title.trim());
        article.setContent(content.trim());
        article.setPublicationDate(new Date());

        return article;
    }
}
