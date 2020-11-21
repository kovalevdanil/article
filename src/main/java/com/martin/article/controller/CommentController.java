package com.martin.article.controller;

import com.martin.article.dto.CommentDto;
import com.martin.article.dto.UserDto;
import com.martin.article.exception.article.ArticleNotFoundException;
import com.martin.article.exception.article.DislikeIsSetException;
import com.martin.article.exception.article.LikeIsSetException;
import com.martin.article.exception.comment.CommentNotFoundException;
import com.martin.article.form.CommentForm;
import com.martin.article.globals.LikeActionEnum;
import com.martin.article.model.Article;
import com.martin.article.model.Comment;
import com.martin.article.model.User;
import com.martin.article.repository.ArticleRepository;
import com.martin.article.repository.CommentRepository;
import com.martin.article.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/comments", produces = APPLICATION_JSON_VALUE)
public class CommentController {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentController(ArticleRepository articleRepository, TagRepository tagRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping
    public ResponseEntity<?> getComments(@RequestParam(name = "slug") String slug,
                                         @RequestParam(name = "nesting", required = false, defaultValue = "0") Integer nesting)
            throws ArticleNotFoundException {
        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        var comments = article.getComments().stream().filter(c -> c.getReplyTo() == null).map(c -> new CommentDto(c, nesting)).collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getComment(@PathVariable(name = "id") Long id) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        return ResponseEntity.ok(new CommentDto(comment, 0));
    }

    @PostMapping
    public ResponseEntity<?> postComment(@RequestParam(name = "slug") String slug,
                                         @RequestBody CommentForm form,
                                         @AuthenticationPrincipal User user)
        throws ArticleNotFoundException{

        Article article = articleRepository.findBySlug(slug).orElseThrow(ArticleNotFoundException::new);
        Comment comment = form.toComment();
        comment.setAuthor(user);
        comment.setArticle(article);

        commentRepository.save(comment);

        return new ResponseEntity<>(new CommentDto(comment, 0), HttpStatus.CREATED);
    }

    @PostMapping("{id}/reply")
    public ResponseEntity<?> postCommentReply(@PathVariable(name = "id") Long id,
                                              @RequestBody CommentForm form,
                                              @AuthenticationPrincipal User user) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        Comment reply = form.toComment();
        reply.setAuthor(user);
        reply.setArticle(comment.getArticle());
        reply.setReplyTo(comment);

        commentRepository.save(reply);

        return new ResponseEntity<>(new CommentDto(reply, 0), HttpStatus.CREATED);
    }

    @PostMapping("{id}/like")
    public ResponseEntity<?> postCommentLike(@PathVariable(name = "id") Long id,
                                             @RequestParam(name = "action") LikeActionEnum action,
                                             @AuthenticationPrincipal User user)
            throws CommentNotFoundException, DislikeIsSetException {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        if (comment.userDisliked(user))
            throw new DislikeIsSetException();

        if (action.equals(LikeActionEnum.SET))
            comment.addUserLiked(user);
        else if (action.equals(LikeActionEnum.UNSET))
            comment.removeUserLiked(user);

        commentRepository.save(comment);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("{id}/dislike")
    public ResponseEntity<?> postCommentDislike(@PathVariable(name = "id") Long id,
                                             @RequestParam(name = "action") LikeActionEnum action,
                                             @AuthenticationPrincipal User user)
            throws CommentNotFoundException, LikeIsSetException {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        if (comment.userDisliked(user))
            throw new LikeIsSetException();

        if (action.equals(LikeActionEnum.SET))
            comment.addUserDisliked(user);
        else if (action.equals(LikeActionEnum.UNSET))
            comment.removeUserDisliked(user);

        commentRepository.save(comment);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("{id}/likes")
    public ResponseEntity<?> getCommentUsersLike(@PathVariable(name = "id") Long id) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        return ResponseEntity.ok(comment.getUsersLiked().stream().map(UserDto::new).collect(Collectors.toList()));
    }

    @GetMapping("{id}/dislikes")
    public ResponseEntity<?> getCommentUsersDislike(@PathVariable(name = "id") Long id) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        return ResponseEntity.ok(comment.getUsersDisliked().stream().map(UserDto::new).collect(Collectors.toList()));
    }
}
