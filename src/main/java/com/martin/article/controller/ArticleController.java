package com.martin.article.controller;

import com.github.slugify.Slugify;
import com.martin.article.dto.ArticleDto;
import com.martin.article.exception.article.ArticleNotFoundException;
import com.martin.article.exception.article.ArticlePermissionDeniedException;
import com.martin.article.form.ArticleForm;
import com.martin.article.model.Article;
import com.martin.article.model.User;
import com.martin.article.repository.ArticleRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/articles", produces = APPLICATION_JSON_VALUE)
public class ArticleController {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleController(ArticleRepository articleRepository){
        this.articleRepository = articleRepository;
    }



    @GetMapping
    public ResponseEntity<?> getArticles(@RequestParam(name = "page", required = false) Integer page,
                                         @RequestParam(name = "size", required = false) Integer size){
        if (page == null || page < 0)
            page = 0;
        if (size == null || size < 0)
            size = 10;

        List<ArticleDto> articles = articleRepository.findAll(PageRequest.of(page, size))
                .stream().map(ArticleDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(articles);
    }

    @GetMapping("{slug}")
    public ResponseEntity<?> getArticle(@PathVariable(name = "slug") String slug) throws ArticleNotFoundException {
        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        return ResponseEntity.ok(new ArticleDto(article));
    }

    @PostMapping
    public ResponseEntity<ArticleDto> postArticle(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody ArticleForm form){
        Article article = form.toArticle();
        article.setAuthor(user);

        String slug = new Slugify().slugify(article.getTitle());
        int sameSlugCount = articleRepository.countBySlug(slug + "%");
        if (sameSlugCount != 0)
            slug = slug + "-" + sameSlugCount;
        article.setSlug(slug);

        articleRepository.save(article);
        return new ResponseEntity<>(new ArticleDto(article), HttpStatus.CREATED);
    }

    @DeleteMapping("{slug}")
    public ResponseEntity<?> deleteArticle(@PathVariable(name = "slug") String slug,
                                           @AuthenticationPrincipal User user)
            throws ArticleNotFoundException, ArticlePermissionDeniedException {
        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        if (!article.getAuthor().getId().equals(user.getId()))
            throw new ArticlePermissionDeniedException();

        articleRepository.delete(article);
        return ResponseEntity.noContent().build();
    }
}
