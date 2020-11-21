package com.martin.article.model;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "published_date", columnDefinition = "TIMESTAMP NOT NULL DEFAULT now()")
    private Date publishedDate;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(targetEntity = Comment.class)
    private Comment replyTo;

    @OneToMany(targetEntity = Comment.class, mappedBy = "replyTo")
    private List<Comment> replies;

    @ManyToOne(targetEntity = Article.class)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "comment_user_liked",
            joinColumns = {@JoinColumn(name = "comment_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> usersLiked;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "comment_user_disliked",
            joinColumns = {@JoinColumn(name = "comment_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> usersDisliked;

    public void addUserLiked(User user){
       if (!userLiked(user))
           usersLiked.add(user);
    }
    public void addUserDisliked(User user){
        if (!userDisliked(user))
            usersDisliked.add(user);
    }

    public boolean removeUserLiked(User user){
        return usersLiked.remove(user);
    }

    public boolean removeUserDisliked(User user){
        return usersDisliked.remove(user);
    }

    public boolean userLiked(User user){
        return usersLiked.stream().anyMatch(u -> u.getId().equals(user.getId()));
    }

    public boolean userDisliked(User user){
        return usersDisliked.stream().anyMatch(u -> u.getId().equals(user.getId()));
    }
}
