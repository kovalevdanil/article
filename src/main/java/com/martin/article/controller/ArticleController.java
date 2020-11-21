package com.martin.article.controller;

import com.github.slugify.Slugify;
import com.martin.article.dto.ArticleDto;
import com.martin.article.dto.TagDto;
import com.martin.article.dto.UserDto;
import com.martin.article.exception.article.ArticleNotFoundException;
import com.martin.article.exception.article.ArticlePermissionDeniedException;
import com.martin.article.exception.article.DislikeIsSetException;
import com.martin.article.exception.article.LikeIsSetException;
import com.martin.article.exception.tag.TagNotFoundException;
import com.martin.article.form.ArticleForm;
import com.martin.article.globals.LikeActionEnum;
import com.martin.article.model.Article;
import com.martin.article.model.Tag;
import com.martin.article.model.User;
import com.martin.article.repository.ArticleRepository;
import com.martin.article.repository.TagRepository;
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
    private final TagRepository tagRepository;

    @Autowired
    public ArticleController(ArticleRepository articleRepository, TagRepository tagRepository){
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
    }



    @GetMapping
    public ResponseEntity<?> getArticles(@RequestParam(name = "page", required = false) Integer page,
                                         @RequestParam(name = "size", required = false) Integer size,
                                         @RequestParam(name = "tags", required = false) String tagsString){
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

    @DeleteMapping("{slug}")
    public ResponseEntity<?> deleteArticle(@PathVariable(name = "slug") String slug,
                                           @AuthenticationPrincipal User user)
            throws ArticleNotFoundException, ArticlePermissionDeniedException {
        Article article = getArticleAndCheckPermission(slug ,user);

        articleRepository.delete(article);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{slug}")
    public ResponseEntity<?> patchArticle(@PathVariable(name = "slug") String slug,
                                          @RequestBody ArticleForm articleForm,
                                          @AuthenticationPrincipal User user)
            throws ArticleNotFoundException, ArticlePermissionDeniedException {

        Article article = getArticleAndCheckPermission(slug ,user);
        if (articleForm.getContent() != null){
            article.setContent(articleForm.getContent());
        }
        if (articleForm.getTitle() != null){
            article.setTitle(articleForm.getTitle());
        }


        articleRepository.save(article);
        return ResponseEntity.noContent().build();
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

    @GetMapping("{slug}/tags")
    public ResponseEntity<Iterable<TagDto>> getArticleTags(@PathVariable(name = "slug") String slug) throws ArticleNotFoundException {
        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        return ResponseEntity.ok(article.getTags().stream().map(TagDto::new).collect(Collectors.toList()));
    }

    @PostMapping("{slug}/tags")
    public ResponseEntity<?> postArticleTags(@PathVariable(name = "slug") String slug,
                                             @RequestParam(name = "tagId") Long tagId,
                                             @AuthenticationPrincipal User user)
            throws ArticleNotFoundException, TagNotFoundException, ArticlePermissionDeniedException {
        Article article = getArticleAndCheckPermission(slug, user);
        Tag tag = tagRepository.findById(tagId).orElseThrow(TagNotFoundException::new);

        article.addTag(tag);
        articleRepository.save(article);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{slug}/tags")
    public ResponseEntity<?> deleteArticleTag(@PathVariable(name = "slug") String slug,
                                               @RequestParam(name = "tagId") Long tagId,
                                               @AuthenticationPrincipal User user)
        throws ArticleNotFoundException, ArticlePermissionDeniedException{

        Article article = getArticleAndCheckPermission(slug, user);
        article.setTags(article.getTags().stream().filter(t -> !t.getId().equals(tagId)).collect(Collectors.toList()));
        articleRepository.save(article);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("{slug}/like")
    public ResponseEntity<?> postArticleLike(@PathVariable(name = "slug") String slug,
                                             @RequestParam(name = "action") LikeActionEnum action,
                                             @AuthenticationPrincipal User user)
            throws ArticleNotFoundException, DislikeIsSetException {
        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        if (article.userDisliked(user))
            throw new DislikeIsSetException();
        if (action.equals(LikeActionEnum.SET)){
            article.addUserLiked(user);
        }
        else if (action.equals(LikeActionEnum.UNSET)){
            article.removeUserLiked(user);
        }
        articleRepository.save(article);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("{slug}/dislike")
    public ResponseEntity<?> postArticleDislike(@PathVariable(name = "slug") String slug,
                                                @RequestParam(name = "action") LikeActionEnum action,
                                                @AuthenticationPrincipal User user)
            throws ArticleNotFoundException, LikeIsSetException {
        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        if (article.userLiked(user))
            throw new LikeIsSetException();
        if (action.equals(LikeActionEnum.SET)){
            article.addUserDisliked(user);
        }
        else if (action.equals(LikeActionEnum.UNSET)){
            article.removeUserDisliked(user);
        }

        articleRepository.save(article);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("{slug}/likes")
    public ResponseEntity<?> getArticleUsersLiked(@PathVariable(name = "slug") String slug) throws ArticleNotFoundException {
        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        return ResponseEntity.ok(article.getUsersLiked().stream().map(UserDto::new).collect(Collectors.toList()));
    }

    @GetMapping("{slug}/dislikes")
    public ResponseEntity<?> getArticleUsersDisliked(@PathVariable(name = "slug") String slug) throws ArticleNotFoundException {
        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        return ResponseEntity.ok(article.getUsersDisliked().stream().map(UserDto::new).collect(Collectors.toList()));
    }

    private Article getArticleAndCheckPermission(String slug, User user) throws ArticlePermissionDeniedException, ArticleNotFoundException {
        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        if (!article.getAuthor().getId().equals(user.getId()))
            throw new ArticlePermissionDeniedException();
        return article;
    }
}
